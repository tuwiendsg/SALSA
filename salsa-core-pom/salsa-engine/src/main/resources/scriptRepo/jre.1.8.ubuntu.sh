echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
apt-add-repository -y ppa:webupd8team/java 
apt-get update 
apt-get install -y axel oracle-java8-installer 
apt-get autoclean
echo "export JAVA_HOME=/usr/lib/jvm/java-7-oracle" >> /etc/profile
. /etc/profile
