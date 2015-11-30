package git

import (
	"log"
	"os"
	"os/exec"
)

// Gitter performs git operations. Works through os/exec.
type Gitter struct {
	git string
}

// New returns a configured *Gitter or an error.
func New() (*Gitter, error) {
	g, err := exec.LookPath("git")
	if err != nil {
		return nil, err
	}
	return &Gitter{
		git: g,
	}, nil
}

// Clone clones a git repo to a name
func (g *Gitter) Clone(repo, name string) error {
	cmd := exec.Command(g.git, "clone", repo, name)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// CheckoutBranch cd's into repoPath and checks out the branch.
func (g *Gitter) CheckoutBranch(repoPath, branch string) error {
	curdir, err := os.Getwd()
	if err != nil {
		return err
	}
	if err = os.Chdir(repoPath); err != nil {
		return err
	}
	defer func() {
		if err = os.Chdir(curdir); err != nil {
			log.Print(err)
		}
	}()
	cmd := exec.Command(g.git, "checkout", branch)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}

// Pull cd's into repoPath and pulls the current branch.
func (g *Gitter) Pull(repoPath string) error {
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
	cmd := exec.Command(g.git, "pull")
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}
