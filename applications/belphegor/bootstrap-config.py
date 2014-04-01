#! /usr/bin/env python

import glob
import json
import os
import re
import subprocess
import sys
import urllib

from optparse import OptionParser

# The base URLs for Jenkins and the last successful Clavin build.
jenkins_base = 'http://watson.iplantcollaborative.org/hudson'
clavin_build = '{0}/job/Clavin/lastSuccessfulBuild'.format(jenkins_base)

def validate(f, value, msg):
    """Performs an arbitrary validation."""
    if not f(value):
        print >> sys.stderr, 'ERROR: {0}'.format(msg)
        sys.exit(1)

def validate_dir(path, msg):
    """Verifies that a path exists and refers to a directory."""
    validate(os.path.isdir, path, msg)

def validate_file(path, msg):
    """Verifies that a path exists and refers to a regular file."""
    validate(os.path.isfile, path, msg)

def validate_repo(path):
    """Verifies that a git repository exists in a directory."""
    repo = os.path.basename(path)
    msg  = 'A clone of {0} must be available in {1}'.format(repo, path)
    validate_dir(path, msg)
    validate_dir(os.path.join(path, '.git'), msg)

def template_dir(path):
    """Builds the path to the configulon template dir."""
    return os.path.join(path, 'templates')

def envs_file(path):
    """Builds the path to the configulon environments file."""
    return os.path.join(path, 'environments.clj')

def validate_configulon(path):
    """Validates the configulon git repository."""
    validate_repo(path)
    msg = 'The templatized version of configulon is required'
    validate_dir(template_dir(path), msg)
    validate_file(envs_file(path), msg)

def get_clavin_jar_name():
    """
    Gets the name of most recent Clavin uberjar file.  This function assumes
    that the files are lexically sortable, which may not be the case when we
    get to multi-digit version numbers.  The Jenkins job should only retain
    the latest uberjar file, however, so this shouldn't be a problem.
    """
    url = '{0}/api/json'.format(clavin_build)
    artifacts = json.loads(urllib.urlopen(url).read())['artifacts']
    names = [
        artifact['fileName'] for artifact in artifacts
           if re.match('^clavin-.*?-standalone.jar$', artifact['fileName'])
    ]
    names.sort()
    return names[-1]

def fetch_clavin_jar(name):
    """Retrieves the Clavin jar file from Jenkins."""
    url = '{0}/artifact/target/{1}'.format(clavin_build, name)
    instream = urllib.urlopen(url)
    with open(name, 'w') as f:
        while True:
            chunk = instream.read(4096)
            if chunk == '':
                break
            f.write(chunk)
    instream.close()

# Paths that we need.
configulon = os.path.join('..', 'configulon')
prop_dest  = os.path.join('src', 'main', 'resources')

# Do some preliminary validation to ensure that everything looks okay.
validate_configulon(configulon)
validate_dir(prop_dest, '{0} does not exist'.format(prop_dest))

# Parse the command-line arguments.
parser = OptionParser(description='Generate test config files.')
parser.add_option('-f', '--envs-file', default = envs_file(configulon),
                  help = 'the path to the environments file')
parser.add_option('-d', '--deployment', default = 'de-2',
                  help = 'the name of the deployment to use')
(options, args) = parser.parse_args()

# Get the name of the Clavin jar file and fetch it if necessary.
clavin_jar = get_clavin_jar_name()
if not os.path.exists(clavin_jar):
    fetch_clavin_jar(clavin_jar)

# Generate the properties files.
cmd = [
    'java', '-jar', clavin_jar, 'files',
    '-f', options.envs_file,
    '-t', template_dir(configulon),
    '-d', options.deployment,
    '--dest', prop_dest,
    'belphegor', 'belphegor-confluence'
]
subprocess.call(cmd)
