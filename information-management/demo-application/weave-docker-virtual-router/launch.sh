# This simple script installs and launches the weave-virtual-router
# This should be run on the host-VM, which create several docker container

sudo curl -L git.io/weave -o /usr/local/bin/weave
sudo chmod a+x /usr/local/bin/weave

weave launch
eval $(weave env)

