package main

import (
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"os/exec"
	"path"
	"path/filepath"

	"github.com/mitchellh/go-homedir"
)

var (
	internalRepo   = flag.String("internal-repo", "", "URI to the internal repo")
	deRepo         = flag.String("de-repo", "", "URI to the external repo")
	internalBranch = flag.String("internal-branch", "dev", "The branch to checkout in the internal ansible repo")
	deAnsibleDir   = flag.String("de-ansible-dir", "ansible", "The name of the ansible directory in the de checkout")
	externalBranch = flag.String("de-repo-branch", "dev", "The branch to checkout in the de repo")
	imageName      = flag.String("docker-image", "discoenv/de-ansible:dev", "The docker image to run, presumably with ansible installed and ready to go.")
	workingDir     = flag.String("working-dir", "~/.de-ansible", "The path to create the external and internal paths under")
	git            string
	docker         string
	home           string
	internalPath   string
	externalPath   string
)

const (
	internal = "internal"
	external = "external"
)

func init() {
	flag.Parse()
}

// Clone clones a git repo to a name
func Clone(repo, name string) error {
	cmd := exec.Command(git, "clone", repo, name)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// CheckoutBranch cd's into repoPath and checks out the branch.
func CheckoutBranch(repoPath, branch string) error {
	curdir, err := os.Getwd()
	if err != nil {
		return err
	}
	if err = os.Chdir(repoPath); err != nil {
		return err
	}
	defer func() {
		if err = os.Chdir(curdir); err != nil {
			log.Fatal(err)
		}
	}()
	cmd := exec.Command(git, "checkout", branch)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// Pull cd's into repoPath and pulls the current branch.
func Pull(repoPath string) error {
	curdir, err := os.Getwd()
	if err != nil {
		return err
	}
	if err = os.Chdir(repoPath); err != nil {
		return err
	}
	defer func() {
		if err = os.Chdir(curdir); err != nil {
			log.Fatal(err)
		}
	}()
	cmd := exec.Command(git, "pull")
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// MoveAnsible moves the ansible subdirectory to the external directory
func MoveAnsible(dePath, externalPath string) error {
	fmt.Printf("Moving DE/ansible to %s", external)
	return os.Rename(path.Join(dePath, *deAnsibleDir), externalPath)
}

// CopyGroupVars copies the group vars from internalPath to externalPath.
func CopyGroupVars(internalPath, externalPath string) error {
	internalGroupVars := path.Join(internalPath, "group_vars")
	externalGroupVars := path.Join(externalPath, "group_vars")
	fmt.Printf("Copying files from %s to %s\n", internalGroupVars, externalGroupVars)
	var copyPaths []string

	visit := func(p string, i os.FileInfo, err error) error {
		if !i.IsDir() {
			fmt.Printf("Found file %s to copy\n", p)
			copyPaths = append(copyPaths, p)
		}
		return err
	}

	err := filepath.Walk(internalGroupVars, visit)
	if err != nil {
		return err
	}

	if _, err := os.Stat(externalGroupVars); os.IsNotExist(err) {
		fmt.Printf("Creating %s\n", externalGroupVars)
		err = os.MkdirAll(externalGroupVars, 0755)
		if err != nil {
			return err
		}
	}

	for _, copyPath := range copyPaths {
		destPath := path.Join(externalGroupVars, path.Base(copyPath))
		fmt.Printf("Copying %s to %s\n", copyPath, destPath)
		contents, err := ioutil.ReadFile(copyPath)
		if err != nil {
			return err
		}
		err = ioutil.WriteFile(destPath, contents, 0644)
		if err != nil {
			return err
		}
	}
	return nil
}

// CopyInventories copies the inventories from the internal repo to the external
// repo
func CopyInventories(internalPath, externalPath string) error {
	internalInventories, err := filepath.Abs(path.Join(internalPath, "inventories"))
	if err != nil {
		return err
	}
	externalInventories, err := filepath.Abs(path.Join(externalPath, "inventories"))
	if err != nil {
		return err
	}

	fmt.Printf("Copying files from %s to %s\n", internalInventories, externalInventories)
	copyPaths := []string{}

	visit := func(p string, i os.FileInfo, err error) error {
		if !i.IsDir() {
			fmt.Printf("Found file %s to copy\n", p)
			copyPaths = append(copyPaths, p)
		}
		return err
	}

	err = filepath.Walk(internalInventories, visit)
	if err != nil {
		return err
	}

	for _, copyPath := range copyPaths {
		destPath := path.Join(externalInventories, path.Base(copyPath))
		fmt.Printf("Copying %s to %s\n", copyPath, destPath)
		contents, err := ioutil.ReadFile(copyPath)
		if err != nil {
			return err
		}
		err = ioutil.WriteFile(destPath, contents, 0644)
		if err != nil {
			return err
		}
	}
	return nil
}

// CopySecret copies the "sudo_secret file from the internal dir to the external
// dir
func CopySecret(internalPath, externalPath string) error {
	origPath, err := filepath.Abs(path.Join(internalPath, "sudo_secret.txt"))
	if err != nil {
		return err
	}
	destPath, err := filepath.Abs(path.Join(externalPath, "sudo_secret.txt"))
	if err != nil {
		return err
	}
	fmt.Printf("Copying %s to %s\n", origPath, destPath)
	contents, err := ioutil.ReadFile(origPath)
	if err != nil {
		return err
	}
	err = ioutil.WriteFile(destPath, contents, 0644)
	if err != nil {
		return err
	}
	return nil
}

// LaunchDocker launches the provided image
func LaunchDocker(imageName, cwd string) error {
	mountArg := fmt.Sprintf("%s:/de-ansible", cwd)
	sshMountArg := fmt.Sprintf("%s:/root/.ssh", home)
	cmd := exec.Command(docker, "run", "-it", "-v", sshMountArg, "-v", mountArg, "-w", "/de-ansible/external", "-e", "ANSIBLE_ROLES_PATH=/de-ansible/external/roles:/de-ansible/internal/roles", imageName)
	cmd.Stdin = os.Stdin
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

func main() {
	if *internalRepo == "" {
		log.Fatal("--internal-repo must be set")
	}
	if *deRepo == "" {
		log.Fatal("--de-repo must be set")
	}
	var err error
	git, err = exec.LookPath("git")
	if err != nil {
		log.Fatal("git couldn't be found in your $PATH")
	}
	docker, err = exec.LookPath("docker")
	if err != nil {
		log.Fatal("docker couldn't be found in your $PATH")
	}
	home, err = homedir.Dir()
	if err != nil {
		log.Fatal(err)
	}

	//Create the ~/.de-ansible directory
	var deAnsiblePath string
	if *workingDir == "~/.de-ansible" {
		deAnsiblePath = path.Join(home, ".de-ansible")
	} else {
		deAnsiblePath, err = filepath.Abs(*workingDir)
		if err != nil {
			log.Fatal(err)
		}
	}
	if _, err = os.Stat(deAnsiblePath); err != nil {
		if err = os.MkdirAll(deAnsiblePath, 0755); err != nil {
			log.Fatal(err)
		}
	}

	dePath := path.Join(deAnsiblePath, "DE")

	// cd into the ~/.de-ansible directory
	if err = os.Chdir(deAnsiblePath); err != nil {
		log.Fatal(err)
	}

	if internalPath, err = filepath.Abs(internal); err != nil {
		log.Fatal(err)
	}

	if externalPath, err = filepath.Abs(external); err != nil {
		log.Fatal(err)
	}

	if _, err := os.Stat(internalPath); err == nil {
		if err = os.RemoveAll(internalPath); err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	if _, err := os.Stat(externalPath); err == nil {
		if err = os.RemoveAll(externalPath); err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	if _, err := os.Stat(dePath); err == nil {
		if err = os.RemoveAll(dePath); err != nil {
			fmt.Print(err)
			os.Exit(-1)
		}
	}

	// clone repos
	if err = Clone(*internalRepo, internal); err != nil {
		log.Fatal(err)
	}

	if err = Clone(*deRepo, "DE"); err != nil {
		log.Fatal(err)
	}

	// Checkout dev branches
	if err = CheckoutBranch(internalPath, *internalBranch); err != nil {
		log.Fatal(err)
	}

	if err = CheckoutBranch(dePath, *externalBranch); err != nil {
		log.Fatal(err)
	}

	// Pull the branchs
	if err = Pull(internalPath); err != nil {
		log.Fatal(err)
	}

	if err = Pull(dePath); err != nil {
		log.Fatal(err)
	}

	// Move the ansible subdir into the external directory
	if err = MoveAnsible(dePath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopyGroupVars(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopyInventories(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopySecret(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}
	reminder := `Don't forget to run:
  cd %s
  export ANSIBLE_ROLES_PATH=%s:%s
  export ANSIBLE_HASH_BEHAVIOUR=merge
  ansible-galaxy install --force -r requirements.yaml
`
	fmt.Printf(reminder, externalPath, path.Join(externalPath, "roles"), path.Join(internalPath, "roles"))
	// if err = LaunchDocker(*imageName, deAnsiblePath); err != nil {
	// 	log.Fatal(err)
	// }

}
