# Discovery Environment Ansible Usage

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

    sudo ansible-galaxy install --force -r requirements.txt

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

__CentOS 5__

    sudo yum install python-simplejson

This should already be done for the development servers.

### curl

Each server needs to have curl on it so ansible can send messages to chat.  curl is provided by
default on CentOS distributions, It needs to be manually installed on Ubuntu systems.

__Ubuntu__

    sudo apt-get install curl

### httplib2

Each server needs to have httplib2 Python library available for the default Python installation. It
needs to be manually installed on Ubuntu systems.

__Ubuntu__

    sudo apt-get install python-httplib2

## Setting Up Your Accounts

You will need passwordless ssh access to each of the servers listed in the inventory for the
environment you're working in.

If you do not already have a ~/.ssh/id_rsa.pub file generated, then run this command to create it:

    ssh-keygen -t rsa

See https://github.com/beautifulcode/ssh-copy-id-for-OSX for instructions on setting up ssh-copy-id
on OS X. Then:

    ssh-copy-id -i <path-to-public-key> <server>

For example:

    ssh-copy-id -i ~/.ssh/id_rsa.pub user@somehost.iplantcollaborative.org

Do that for each of the servers in the inventory. I'd recommend setting up an SSH config that has
entries for each of the servers (include the fully-qualified domain name) first; it will allow you
to skip a lot of typing.

Next, you need to generate entries in ~/.ssh/known_hosts for each of the servers. The easiest way to
do this is to ssh into each of the servers once **after** you've set up passwordless ssh access. You
need to SSH into the fully-qualified domain name of the host. For example:

    ssh somehost.iplantcollaborative.org

You will also need sudo access on those servers for some operations. You should already have this if
you're in the dev group on the servers.

## Using vagrant to deploy to local VMs

* This requires `virtualbox 4.3` and `vagrant 1.7` to be installed.

At the moment, there are three VMs that get provisioned: a CentOS 5.10, a CentOS 6.5, and an Ubuntu
12.04. To bring them up, go into the directory `local-vagrant` and bring up using `vagrant up`.

    cd local-vagrant
    vagrant up
    cd ..

At the moment, only the `systems.yaml`, `elasticsearch.yaml`, and `ampq-brokers.yaml` playbooks can
be run on the VM.

    ansible-playbook -i inventories/dev/local.cfg systems.yaml

## Updating the entire backend

    ansible-playbook -i inventories/dev/de-2.cfg -K deploy-backend.yaml

The playbook will:
* Deploy database changes
* Update services
* Update the condor nodes

The -K option will force a prompt for your sudo password. It needs to be the same across all hosts.

## Updating Services, excluding anon-files and Chinstrap

__Development__

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] [--extra-vars "service_name=<name>"] update[-dev]-services.yaml
    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] --extra-vars "<service>_jar_file=<local path>" upload-dev-services.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional). The optional --extra-vars argument allows
you to specify the name of a single service to update, rather than updating all services.
Run the update-services.yaml playbook to update services via yum.
Run the update-dev-services.yaml playbook to update services with jars built by Jenkins. You may
also include a "backend_dev_build=####" extra-var to specify a Jenkins build number when running
this playbook.
Run the upload-dev-services.yaml playbook to update services with jars built locally. You must
include the --extra-vars argument to specifiy which services to update and where the jar file is
locally. For example:
    --extra-vars="donkey_jar_file=/Users/ipctest/src/DiscoveryEnvironmentBackend/services/Donkey/target/donkey-3.0.1-SNAPSHOT-standalone.jar"

## Updating Just anon-files

__Development__
    ansible-playbook -i inventories/dev/de-2.cfg -K -u <user> update-anon-files.yaml

## Updating Just Chinstrap

__Development__

    ansible-playbook -i inventories/dev/de-2.cfg -K -u <user> update[-dev]-chinstrap.yaml
    ansible-playbook -i inventories/dev/de-2.cfg -K -u <user> --extra-varsw "chinstrap_jar_file=<local path>" upload-dev-chinstrap.yaml

## Updating UI wars

Thre are three playbooks for updating UI WAR files: one for the DE, one for Belphegor, and one to
deploy both of them at once. The `group_vars` files determine where the playbook obtains the WAR
files from. For development environments, the files are obtained from the latest development build
in Jenkins. For QA environments, the files are obtained from the `latest` QA drop symbolic link. For
the production environment, the files are obtained from the `prod` QA drop symbolic link. For QA and
production environments, the QA drop can be selected by specifying the value of the `qa_drop`
variable in the `--extra-vars` command-line option.

__All WAR Files__

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] ui.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional).

__DE Only__

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] de.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional).

__Belphegor Only__

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] belphegor.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional).

## Initializing or Updating the DE database

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] [--extra-vars "tgz_flag=<-q drop-dir>,facepalm_mode=(init|update)"] de-database.yaml

The -i, -K, and -u options are the same as in the other ansible commands.

The --extra-vars will allow you to override a couple of vars for this playbook.

* Overriding the __tgz_flag__ var allows you specify the tarball flag to pass to facepalm. By
  default it uses the `-q latest` flag, which downloads the tarball from the latest QA drop.

    `--extra-vars "tgz_flag='-q x.x.x-QAxx'"`
    or
    `--extra-vars "tgz_flag='-f foo.tar.gz' tgz_file=/path/to/local/foo.tar.gz tgz_name=foo.tar.gz"`

* Overriding the __facepalm_mode__ var allows you to specify whether to do an init of a fresh
  database or update an existing one. The only accepted values are either 'init' or 'update',
  without the quotes. The default is 'update'.

    `--extra-vars "facepalm_mode=init"`

You can specify multiple extra-vars at once by separating them with commas.

## Initializing or Updating the Notifications database

    ansible-playbook -i inventories/dev/de-2.cfg -K [-u <user>] [--extra-vars "tgz_flag=<-q drop-dir>,facepalm_mode=(init|update)"] notifications-database.yaml

The -i, -K, and -u options are the same as in the other ansible commands.

The --extra-vars will allow you to override a couple of vars for this playbook.

* Overriding the __tgz_flag__ var allows you specify the tarball flag to pass to facepalm. By
  default it uses the `-q latest` flag, which downloads the tarball from the latest QA drop.

    `--extra-vars "tgz_flag='-q x.x.x-QAxx'"`
    or
    `--extra-vars "tgz_flag='-f foo.tar.gz' tgz_file=/path/to/local/foo.tar.gz tgz_name=foo.tar.gz"`

* Overriding the __facepalm_mode__ var allows you to specify whether to do an init of a fresh
  database or update an existing one. The only accepted values are either 'init' or 'update',
  without the quotes. The default is 'update'.

    `--extra-vars "facepalm_mode=init"`

You can specify multiple extra-vars at once by separating them with commas.

## Creating and Populating a Drop Directory

    ansible-playbook -i inventories/dev-support/support.cfg [-u <user>] --extra-vars "de_version=1.8.6 drop_number=01" drop-dir.yaml

The de\_version extra var is the DE version number. The drop\_number extra var is the number of the
QA drop being created. Both are required and are used to generate the QA drop name and populate the
drop manifest.

## Updating a yum repository

    ansible-playbook -i inventories/dev-support/support.cfg -K --extra-vars "rpm_src=iplant-dev rpm_dest=iplant-qa rpm_version=4.0.0" update-yum-repo.yaml

The rpm_version extra-var is used to determine which RPMs to copy from the "rpm_src" extra-var
directory to the "rpm_dest" extra-var directory.
The base directory var "rpm_base_dir" is defined in the "yum-repo-update" role as /var/www/html/rpms.
Valid values for "rpm_src" and "rpm_dest" are:
iplant-dev, iplant-qa, iplant-stage, iplant-prod.

## Updating the system packages

The system packages are those in the core repos for the distribution.  Extra packages, like httpd,
need to be manually updated for now.

    ansible-playbook -K -i inventories/dev/de-2.cfg systems.yaml

## Updating the AMQP brokers

    ansible-playbook -K -i inventories/dev/de-2.cfg amqp-brokers.yaml
