#!/bin/sh

pushd ~
tar czf ssh-configs.tar.gz .ssh/
popd
cp ~/ssh-configs.tar.gz .

