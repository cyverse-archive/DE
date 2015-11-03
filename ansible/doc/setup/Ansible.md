# DE Ansible Setup Instructions

## Installing Ansible
See http://docs.ansible.com/intro_installation.html for the long instructions.

There's multiple ways to install ansible. I used Python's 'pip' command to install it on OS X, but
you can also use homebrew or a git checkout.

__pip__

    sudo pip install ansible

__homebrew__

    brew install ansible

__checkout__

    git clone git://github.com/ansible/ansible.git
    cd ./ansible
    source ./hacking/env-setup

If you use the checkout method, add a line to your ~/.profile (on OS X) or your ~/.bashrc file (all
other sane OSes).

__You only need ansible on your local machine. It does not need to be installed on the servers.__

## Installing Third Party Ansible Roles
See http://docs.ansible.com/galaxy.html for more information about managing third party roles.

In brief, you'll need to sign up for galaxy at https://galaxy.ansible.com and execute the following
command in the de-ansible repo.

    ansible-galaxy install --force -r requirements.yaml

## Learning About Ansible

If you just want to use ansible for DE related tasks, move on to the other sections.

If you want to make your own playbooks for existing inventories of servers, read:
http://docs.ansible.com/playbooks_intro.html.

If you want to create a new inventory or modify an existing one, read:
http://docs.ansible.com/intro_inventory.html.

Each playbook uses one or more modules. Modules encapsulate operations that run on servers and make
them idempotent. There are a lot of existing modules, which you can read about at
http://docs.ansible.com/modules_by_category.html.

## Preparing Servers

### simplejson

Each server needs to have the simplejson Python library available for the default Python
installation.

__CentOS 5/6__

    sudo yum install python-simplejson

This should already be done for the development servers.

### curl

Each server needs to have curl on it so ansible can send messages to chat.  curl is provided by
default on CentOS distributions, It needs to be manually installed on Ubuntu systems.

__Ubuntu__

    sudo apt-get install curl

### httplib2

Each server needs to have httplib2 Python library available for the default Python installation. It
needs to be manually installed on Ubuntu and CentOS 6 systems.

##### TODO: Is manual installation still necessary? The private-registry-image-builder role installs it with pip (on CentOS7 systems at least).

__Ubuntu__

    sudo apt-get install python-httplib2

__CentOS 6__

    sudo yum install python-httplib2

## Setting Up Your Accounts

You will need passwordless ssh access to each of the servers listed in the inventory for the
environment you're working in.

### Generate RSA private and public keys

If you do not already have a ~/.ssh/id_rsa.pub file generated, then run this command to create it:

    ssh-keygen -t rsa

### Install ssh-copy-id

See https://github.com/beautifulcode/ssh-copy-id-for-OSX for instructions on setting up ssh-copy-id
on OS X.

### Copying ssh keys

    ssh-copy-id -i <path-to-public-key> <server>

For example:

    ssh-copy-id -i ~/.ssh/id_rsa.pub user@example.iplantcollaborative.org

Do that for each of the servers in the inventory. I'd recommend setting up an SSH config that has
entries for each of the servers (include the fully-qualified domain name) first; it will allow you
to skip a lot of typing.

Next, you need to generate entries in ~/.ssh/known_hosts for each of the servers. The easiest way to
do this is to ssh into each of the servers once **after** you've set up passwordless ssh access. You
need to SSH into the fully-qualified domain name of the host. For example:

    ssh example.iplantcollaborative.org

You will also need sudo access on those servers for some operations. You should already have this if
you're in the dev group on the servers.
