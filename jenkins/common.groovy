def version() {
    readFile('version')
}

def buildenv(cmd) {
    b = "docker run --rm -v \$(pwd):/build -w /build discoenv/buildenv ${cmd}"
    sh b
}

def buildenv_gitcommit() {
    sh "docker inspect -f '{{ (index .Config.Labels \"org.iplantc.de.buildenv.git-ref\")}}' discoenv/buildenv:latest > BUILDENV_GIT_COMMIT"
    readFile('BUILDENV_GIT_COMMIT')
}

def gitcommit() {
    sh "git rev-parse HEAD > GIT_COMMIT"
    readFile('GIT_COMMIT')
}

def user() {
  sh "whoami > WHOAMI"
  readFile('WHOAMI')
}

def dockerbuild(dockerfile, repotag) {
    gc = gitcommit()
    bgc = buildenv_gitcommit()
    v = version()
    cmd = "docker build -f ${dockerfile} --build-arg \'git_commit=${gc}\' --build-arg \'buildenv_git_commit=${bgc}\' --build-arg \'version=${v}\' --pull --rm -t ${repotag} ."
    echo cmd
    sh cmd
}

def dockerpush(repotag) {
  sh "docker push ${repotag}"
}

this
