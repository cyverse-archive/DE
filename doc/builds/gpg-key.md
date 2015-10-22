# Generating GPG Keys for Donkey

The instructions for this were adapted (and shortened) from a
[blog post](http://andys.org.uk/bits/2010/02/02/gnupg-rsa-key-pair-mini-howto/).

### Step 1: create the directory for the keys.

    sudo mkdir /etc/iplant/de/crypto
    sudo chown iplant.iplant /etc/iplant/de/crypto
    sudo chmod 700 /etc/iplant/de/crypto

### Step 2: generate the signing key.

This step and all following steps should be executed from within the iplant
account.

    gpg --homedir=/etc/iplant/de/crypto --gen-key

* Select `RSA (sign only)` for the key type.
* The default key length is okay for our purposes.
* Do not set an expiration time for the key.
* We've been using `iPlant Core Software` name and email address settings.

Note: after accepting the settings, GPG will probably wait for the computer to
perform some operations before proceeding. You can speed up this process by
entering some commands in another shell session.

The GPG output will look something like this:

    gpg: /etc/iplant/de/crypto/trustdb.gpg: trustdb created
    gpg: key 6787B851 marked as ultimately trusted
    public and secret key created and signed.

    gpg: checking the trustdb
    gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
    gpg: depth: 0  valid:   1  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 1u
    pub   2048R/6787B851 2014-06-12
          Key fingerprint = 96F7 E98A BCBC 0388 F1D7  36B3 F1FC C042 6787 B851
    uid                  iPlant Core Software <******@iplantcollaborative.org>

Make a note of the key ID (`6787B851` in this case) before proceeding to the
next step.

### Step 3: create a subkey for encryption.

    gpg --homedir=/etc/iplant/de/crypto --edit-key <key-id>
    addkey

* You'll have to enter the key password again.
* Select `RSA (encrypt only)` for the key type.
* The default key length is okay for our purposes.
* Do not set an expiration time for the key.

### Step 4: save the changes.

    save

This will both save the changes and cause the program to exit.

### Step 5: generate the tarball.

    sudo su -
    cd /etc/iplant/de/crypto
    tar czpvf /path/to/{environment-name}.tar.gz .
