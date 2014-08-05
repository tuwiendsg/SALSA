#!/bin/bash

curl -L https://www.opscode.com/chef/install.sh | sudo bash
mkdir /etc/chef
mv client.rb /etc/chef
mv validation.pem /etc/chef


