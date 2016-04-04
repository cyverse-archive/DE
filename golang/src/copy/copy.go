package copy

import (
	"fmt"
	"io/ioutil"
	"os"
	"path"
	"path/filepath"
)

// Copier copies files/folders from one directory to another.
type Copier struct {
	copyPaths []string
}

// New returns a new *Copier.
func New() *Copier {
	var p []string
	return &Copier{
		copyPaths: p,
	}
}

// FileVisitor is a function passed to filepath.Walk() to perform operations on
// items in a directory. This visitor populates the *Copier with a list of file
// paths to copy.
func (c *Copier) FileVisitor(p string, i os.FileInfo, err error) error {
	if !i.IsDir() {
		fmt.Printf("Found file %s to copy\n", p)
		c.copyPaths = append(c.copyPaths, p)
	}
	return err
}

// Copy is where the action happens. Pass in a visitor and the source and
// destination directories. All directories down to the  destination directory
// will be created if they doe not exist.
func (c *Copier) Copy(visitor filepath.WalkFunc, source, dest string) error {
	if _, err := os.Stat(dest); os.IsNotExist(err) {
		fmt.Printf("Creating %s\n", dest)
		if err := os.MkdirAll(dest, 0755); err != nil {
			return err
		}
	}

	if err := filepath.Walk(source, visitor); err != nil {
		return err
	}

	for _, copyPath := range c.copyPaths {
		destPath := path.Join(dest, path.Base(copyPath))
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

// File copies the contents of source to dest. No existence checking is done.
func File(source, dest string) error {
	contents, err := ioutil.ReadFile(source)
	if err != nil {
		return err
	}
	err = ioutil.WriteFile(dest, contents, 0644)
	if err != nil {
		return err
	}
	return nil
}
