#!/bin/bash

ROOTDIR=`pwd`/chef-repo
COOKBOOKDIR=$ROOTDIR/cookbooks
COOKBOOK=$1
GIT_CLONE=$2

# TODO: add checking if CHEF is existed, in that case, do not duplicate the installation
curl -L https://www.opscode.com/chef/install.sh > install.sh
bash install.sh

wget -q -L http://github.com/opscode/chef-repo/tarball/master
tar -xvzf master
mv opscode-chef-repo* chef-repo
rm master


# Get cookbook
echo GIT CLONE THE COOKBOOK
git clone $GIT_CLONE $COOKBOOKDIR/$1


# Build solo.rb
echo "root = '$ROOTDIR'" > solo.rb
echo "file_cache_path root" >> solo.rb
echo "cookbook_path '$COOKBOOKDIR'" >> solo.rb


# Build solo.json
echo "{" > node.json
echo "\"run_list\": [ \"recipe[$COOKBOOK]\" ]" >> node.json
echo "}" >> node.json

chef-solo -c solo.rb -j node.json