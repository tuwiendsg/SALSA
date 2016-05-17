#!/bin/bash

DOCKER_NODE_ID=$1
DOCKER_INSTANCE_ID=$2

. /etc/salsa.variables

echo \"Running the script for installing SALSA Pioneer\" >> /tmp/salsa.pioneer.log

# Check for wget
if type -p wget; then
    echo found wget executable in PATH
else
    sudo apt-get -q update
    sudo apt-get -q -y install wget
fi

# Check for Java
if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME     
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
fi

REQUIRE_JAVA=1.7

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" > "$REQUIRE_JAVA" ]]; then
        echo version is more than $REQUIRE_JAVA, it will be used
    else         
        echo version is less than $REQUIRE_JAVA, attempt to install new jre

        cd /opt

        wget -q --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-linux-x64.tar.gz"
        tar -xzf jre-8u60-linux-x64.tar.gz
        cd jre1.8.0_60/

        update-alternatives --install /usr/bin/java java /opt/jre1.8.0_60/bin/java 100
        sudo update-alternatives --set java /opt/jre1.8.0_60/bin/java

        export JRE_HOME=/opt/jre1.8.0_60
        export PATH=$PATH:/opt/java/jdk1.8.0_45/bin:/opt/java/jdk1.8.0_45/jre/bin
    fi
fi



TMPFILE=salsa.variables.$DOCKER_NODE_ID.$DOCKER_INSTANCE_ID

echo "SALSA_SERVICE_ID=$SALSA_SERVICE_ID" > $TMPFILE
echo "SALSA_TOPOLOGY_ID=$SALSA_TOPOLOGY_ID" >> $TMPFILE
echo "SALSA_NODE_ID=$DOCKER_NODE_ID" >> $TMPFILE
echo "SALSA_REPLICA=$DOCKER_INSTANCE_ID" >> $TMPFILE

echo "SALSA_TOSCA_FILE=$SALSA_TOSCA_FILE" >> $TMPFILE
echo "SALSA_WORKING_DIR=$SALSA_WORKING_DIR/$DOCKER_NODE_ID.$DOCKER_INSTANCE_ID" >> $TMPFILE
echo "SALSA_PIONEER_WEB=$SALSA_PIONEER_WEB" >> $TMPFILE
echo "SALSA_PIONEER_RUN=$SALSA_PIONEER_RUN" >> $TMPFILE
echo "SALSA_CENTER_ENDPOINT=$SALSA_CENTER_ENDPOINT" >> $TMPFILE

if [ -n "$BROKER" ]; then
    echo "BROKER=$BROKER" >> $TMPFILE
fi

if [ -n "$BROKER_TYPE" ]; then
    echo "BROKER_TYPE=$BROKER_TYPE" >> $TMPFILE
fi



# export to use right now the Working dir
SALSA_WORKING_DIR=$SALSA_WORKING_DIR/$DOCKER_NODE_ID.$DOCKER_INSTANCE_ID
export SALSA_WORKING_DIR

mkdir -p $SALSA_WORKING_DIR
mv -f $TMPFILE $SALSA_WORKING_DIR/salsa.variables
cd $SALSA_WORKING_DIR

wget -qN --content-disposition $SALSA_PIONEER_WEB

java -jar salsa-pioneer.jar startserver
