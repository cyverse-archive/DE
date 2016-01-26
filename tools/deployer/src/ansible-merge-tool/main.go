package main

import (
	"copy"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"os/exec"
	"path"
	"path/filepath"

	"git"

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

// CopyCompose copies the files needed to launch docker-compose from the merged
// directory.
func CopyCompose(internalPath, externalPath string) error {
	origPath, err := filepath.Abs(path.Join(internalPath, "docker-compose-configs.yml"))
	if err != nil {
		return err
	}
	destPath, err := filepath.Abs(path.Join(externalPath, "docker-compose-configs.yml"))
	if err != nil {
		return err
	}
	fmt.Printf("Copying %s to %s\n", origPath, destPath)
	contents, err := ioutil.ReadFile(origPath)
	if err != nil {
		return err
	}
	if err = ioutil.WriteFile(destPath, contents, 0644); err != nil {
		return err
	}
	origPath, err = filepath.Abs(path.Join(internalPath, "docker-compose.yml"))
	if err != nil {
		return err
	}
	destPath, err = filepath.Abs(path.Join(externalPath, "docker-compose.yml"))
	if err != nil {
		return err
	}
	fmt.Printf("Copying %s to %s\n", origPath, destPath)
	contents, err = ioutil.ReadFile(origPath)
	if err != nil {
		return err
	}
	if err = ioutil.WriteFile(destPath, contents, 0644); err != nil {
		return err
	}
	return nil
}

// CopyRemoteFiles copies files from the internal remote_files directory to
// the external remote files directory.
func CopyRemoteFiles(internalPath, externalPath string) error {
	var (
		err                              error
		internalRemotes, externalRemotes string
	)
	if internalRemotes, err = filepath.Abs(path.Join(internalPath, "remote_files")); err != nil {
		return err
	}
	if externalRemotes, err = filepath.Abs(path.Join(externalPath, "remote_files")); err != nil {
		return err
	}
	_, err = os.Stat(internalRemotes)
	if os.IsNotExist(err) {
		log.Print(err)
		return nil
	}
	if err != nil {
		return err
	}
	remotesCopier := copy.New()
	if err = remotesCopier.Copy(remotesCopier.FileVisitor, internalRemotes, externalRemotes); err != nil {
		return err
	}
	return nil
}

// CopyPlaybooks copies the contents of the internal playbooks directory to the
// external playbooks directory
func CopyPlaybooks(internalPath, externalPath string) error {
	internalPlaybooks, err := filepath.Abs(path.Join(internalPath, "playbooks"))
	if err != nil {
		return err
	}
	externalPlaybooks, err := filepath.Abs(path.Join(externalPath, "playbooks"))
	if err != nil {
		return err
	}
	playbookCopier := copy.New()
	return playbookCopier.Copy(playbookCopier.FileVisitor, internalPlaybooks, externalPlaybooks)
}

func globberCopy(glob, externalPath string) error {
	matches, err := filepath.Glob(glob)
	if err != nil {
		return err
	}
	for _, m := range matches {
		p := path.Join(externalPath, filepath.Base(m))
		if err = copy.File(m, p); err != nil {
			return err
		}
	}
	return nil
}

// CopyToplevelFiles copies any additional .yaml or .yml files in the top-level
// that it finds.
func CopyToplevelFiles(internalPath, externalPath string) error {
	var err error
	glob := path.Join(internalPath, "*.yaml")
	if err = globberCopy(glob, externalPath); err != nil {
		return err
	}
	glob = path.Join(internalPath, "*.yml")
	return globberCopy(glob, externalPath)
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
	git, err := git.New()
	if err != nil {
		log.Fatal("git couldn't be found in your $PATH")
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
	if err = git.Clone(*internalRepo, internal); err != nil {
		log.Fatal(err)
	}

	if err = git.Clone(*deRepo, "DE"); err != nil {
		log.Fatal(err)
	}

	// Checkout dev branches
	if err = git.CheckoutBranch(internalPath, *internalBranch); err != nil {
		log.Fatal(err)
	}

	if err = git.CheckoutBranch(dePath, *externalBranch); err != nil {
		log.Fatal(err)
	}

	// Pull the branchs
	if err = git.Pull(internalPath); err != nil {
		log.Fatal(err)
	}

	if err = git.Pull(dePath); err != nil {
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

	if err = CopyCompose(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopyRemoteFiles(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopyPlaybooks(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

	if err = CopyToplevelFiles(internalPath, externalPath); err != nil {
		log.Fatal(err)
	}

}
