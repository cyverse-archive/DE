package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"git"
	"io/ioutil"
	"log"
	"os"
	"os/exec"
	"path"
	"path/filepath"
	"strings"
)

var (
	externalRepo    = flag.String("git-repo-external", "", "The external git repository to clone")
	externalBranch  = flag.String("git-branch-external", "dev", "The git branch to check out in the external repo")
	gitRepo         = flag.String("git-repo-internal", "", "The internal git repository to clone")
	gitBranch       = flag.String("git-branch-internal", "dev", "The git branch to check out in the internal repo")
	account         = flag.String("account", "discoenv", "The Docker account to use")
	repo            = flag.String("repo", "", "The Docker repo to pull")
	vaultPass       = flag.String("vault-pass", "", "The path to the ansible vault password file")
	secretFile      = flag.String("secret", "", "The file encrypted by ansible-vault")
	inventory       = flag.String("inventory", "", "The ansible inventory to use")
	tag             = flag.String("tag", "dev", "The docker tag to pull from")
	user            = flag.String("user", "", "The sudo user to use with the ansible command")
	service         = flag.String("service", "", "The service to restart on the host")
	serviceVar      = flag.String("service-var", "", "The service var in the group vars")
	serviceInvGroup = flag.String("service-inv-group", "", "The inventory group for the service")
	playbook        = flag.String("playbook", "", "The ansible playbook to use")
	ansibleSSHPort  = flag.String("ssh-port", "22", "The ssh port that ansible should use when restarting services")
)

const (
	internalDir = "internal-deployer-checkout"
	externalDir = "external-deployer-checkout"
)

func init() {
	flag.Parse()
}

// ExtraVars represents the options pass to the various ansible-playbook commands.
type ExtraVars struct {
	Config        bool     `json:"config"`
	LoggingConfig bool     `json:"logging_config"`
	DockerPull    bool     `json:"docker_pull"`
	SystemdEnable bool     `json:"systemd_enable"`
	Services      []string `json:"services"`
}

// NewExtraVars returns a new instance of ExtraVars
func NewExtraVars(config, logging, pull, systemd bool, services []string) *ExtraVars {
	return &ExtraVars{
		Config:        config,
		LoggingConfig: logging,
		DockerPull:    pull,
		SystemdEnable: systemd,
		Services:      services,
	}
}

func (e *ExtraVars) String() string {
	marshaled, err := json.Marshal(e)
	if err != nil {
		return ""
	}
	return string(marshaled[:])
}

func main() {
	if *gitRepo == "" {
		fmt.Println("--git-repo-internal must be set.")
		os.Exit(-1)
	}

	if *externalRepo == "" {
		fmt.Println("--git-repo-external must be set.")
	}

	if *account == "" {
		fmt.Println("--account must be set.")
		os.Exit(-1)
	}

	if *repo == "" {
		fmt.Println("--repo must be set.")
		os.Exit(-1)
	}

	if *vaultPass == "" {
		fmt.Println("--vault-pass must be set.")
		os.Exit(-1)
	}

	if *secretFile == "" {
		fmt.Println("--secret must be set")
		os.Exit(-1)
	}

	if *inventory == "" {
		fmt.Println("--inventory must be set")
		os.Exit(-1)
	}

	if *user == "" {
		fmt.Println("--user must be set")
		os.Exit(-1)
	}

	if *service == "" {
		fmt.Println("--service must be set")
		os.Exit(-1)
	}

	if *serviceVar == "" {
		fmt.Println("--service-var must be set")
		os.Exit(-1)
	}

	if *serviceInvGroup == "" {
		fmt.Println("--service-inv-group must be set")
		os.Exit(-1)
	}

	if *playbook == "" {
		fmt.Println("--playbook must be set")
		os.Exit(-1)
	}

	git, err := git.New()
	if err != nil {
		log.Fatal(err)
	}

	ansiblePlaybook, err := exec.LookPath("ansible-playbook")
	if err != nil {
		log.Fatal(err)
	}

	ansible, err := exec.LookPath("ansible")
	if err != nil {
		log.Fatal(err)
	}

	if _, err := os.Stat(internalDir); err == nil {
		if err = os.RemoveAll(internalDir); err != nil {
			log.Fatal(err)
		}
	}

	if _, err := os.Stat(externalDir); err == nil {
		if err = os.RemoveAll(externalDir); err != nil {
			log.Fatal(err)
		}
	}

	if _, err := os.Stat("DE"); err == nil {
		if err = os.RemoveAll("DE"); err != nil {
			log.Fatal(err)
		}
	}

	fmt.Printf("Cloning the internal repo %s \n", *gitRepo)
	if err = git.Clone(*gitRepo, internalDir); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Cloning the external repo %s\n", *externalRepo)
	if err = git.Clone(*externalRepo, "DE"); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Checking out the %s branch from the internal repo\n", *gitBranch)
	if err = git.CheckoutBranch(internalDir, *gitBranch); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Pulling the %s branch from the internal repo\n", *gitBranch)
	if err = git.Pull(internalDir); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Checking out the %s branch from the external repo\n", *externalBranch)
	if err = git.CheckoutBranch("DE", *externalBranch); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Pulling the %s branch from the external repo\n", *externalBranch)
	if err = git.Pull("DE"); err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Moving DE/ansible to %s", externalDir)
	err = os.Rename("DE/ansible", externalDir)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	internalGroupVars, err := filepath.Abs(path.Join(internalDir, "group_vars"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
	externalGroupVars, err := filepath.Abs(path.Join(externalDir, "group_vars"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	fmt.Printf("Copying files from %s to %s\n", internalGroupVars, externalGroupVars)
	var copyPaths []string

	visit := func(p string, i os.FileInfo, err error) error {
		if !i.IsDir() {
			fmt.Printf("Found file %s to copy\n", p)
			copyPaths = append(copyPaths, p)
		}
		return err
	}

	err = filepath.Walk(internalGroupVars, visit)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	if _, err := os.Stat(externalGroupVars); os.IsNotExist(err) {
		fmt.Printf("Creating %s\n", externalGroupVars)
		err = os.MkdirAll(externalGroupVars, 0755)
		if err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	for _, copyPath := range copyPaths {
		destPath := path.Join(externalGroupVars, path.Base(copyPath))
		fmt.Printf("Copying %s to %s\n", copyPath, destPath)
		contents, err := ioutil.ReadFile(copyPath)
		if err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
		err = ioutil.WriteFile(destPath, contents, 0644)
		if err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	internalInventories, err := filepath.Abs(path.Join(internalDir, "inventories"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
	externalInventories, err := filepath.Abs(path.Join(externalDir, "inventories"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	fmt.Printf("Copying files from %s to %s\n", internalInventories, externalInventories)
	copyPaths = []string{}

	err = filepath.Walk(internalInventories, visit)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	for _, copyPath := range copyPaths {
		destPath := path.Join(externalInventories, path.Base(copyPath))
		fmt.Printf("Copying %s to %s\n", copyPath, destPath)
		contents, err := ioutil.ReadFile(copyPath)
		if err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
		err = ioutil.WriteFile(destPath, contents, 0644)
		if err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	origPath, err := filepath.Abs(path.Join(internalDir, "sudo_secret.txt"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
	destPath, err := filepath.Abs(path.Join(externalDir, "sudo_secret.txt"))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
	fmt.Printf("Copying %s to %s\n", origPath, destPath)
	contents, err := ioutil.ReadFile(origPath)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
	err = ioutil.WriteFile(destPath, contents, 0644)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	fmt.Printf("cd'ing into %s\n", externalDir)
	err = os.Chdir(externalDir)
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	pullVars := NewExtraVars(false, false, true, false, []string{*serviceVar}).String()
	fmt.Printf("Updating %s/%s:%s with ansible\n", *account, *repo, *tag)
	cmd := exec.Command(
		ansiblePlaybook,
		"-e",
		fmt.Sprintf("@%s", *secretFile),
		fmt.Sprintf("--vault-password-file=%s", *vaultPass),
		"-i",
		*inventory,
		"--sudo",
		"-u",
		*user,
		"--extra-vars",
		pullVars,
		*playbook,
	)
	fmt.Printf("%s %s\n", cmd.Path, strings.Join(cmd.Args, " "))
	output, err := cmd.CombinedOutput()
	fmt.Println(string(output[:]))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	configVars := NewExtraVars(true, true, false, true, []string{*serviceVar}).String()
	fmt.Printf("Configuring %s with ansible\n", *repo)
	cmd = exec.Command(
		ansiblePlaybook,
		"-e",
		fmt.Sprintf("@%s", *secretFile),
		fmt.Sprintf("--vault-password-file=%s", *vaultPass),
		"-i",
		*inventory,
		"--sudo",
		"-u",
		*user,
		"--extra-vars",
		configVars,
		*playbook,
	)
	fmt.Printf("%s %s\n", cmd.Path, strings.Join(cmd.Args, " "))
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err = cmd.Run()
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}

	// serviceVars := NewExtraVars
	// fmt.Printf("Updating service file for %s with ansible\n", *repo)
	// cmd = exec.Command(
	// 	ansiblePlaybook,
	// 	"-e",
	// 	fmt.Sprintf("@%s", *secretFile),
	// 	fmt.Sprintf("--vault-password-file=%s", *vaultPass),
	// 	"-i",
	// 	*inventory,
	// 	"--sudo",
	// 	"-u",
	// 	*user,
	// 	"--tags",
	// 	*serviceTag,
	// 	*playbook,
	// )
	// fmt.Printf("%s %s\n", cmd.Path, strings.Join(cmd.Args, " "))
	// output, err = cmd.CombinedOutput()
	// fmt.Println(string(output[:]))
	// if err != nil {
	// 	fmt.Print(err)
	// 	os.Exit(-1)
	// }

	fmt.Printf("Restarting %s with ansible\n", *repo)
	cmd = exec.Command(
		ansible,
		*serviceInvGroup,
		"-e",
		fmt.Sprintf("@%s", *secretFile),
		"-e",
		fmt.Sprintf("ansible_ssh_port=%s", *ansibleSSHPort),
		fmt.Sprintf("--vault-password-file=%s", *vaultPass),
		"-i",
		*inventory,
		"--sudo",
		"-u",
		*user,
		"-m", "service",
		"-a", fmt.Sprintf("name=%s state=restarted", *service),
	)
	fmt.Printf("%s %s\n", cmd.Path, strings.Join(cmd.Args, " "))
	output, err = cmd.CombinedOutput()
	fmt.Println(string(output[:]))
	if err != nil {
		fmt.Print(err)
		os.Exit(-1)
	}
}
