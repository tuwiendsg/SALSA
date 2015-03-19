#!/bin/bash

DOCKER_NODE_ID=$1
DOCKER_INSTANCE_ID=$2

. /etc/salsa.variables

echo \"Running the script for installing SALSA Pioneer\" >> /tmp/salsa.pioneer.log

# Check for Java
if type -p java; then
    echo "Found java executable in PATH" >> /tmp/salsa.pioneer.log
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Found java executable in JAVA_HOME" >> /tmp/salsa.pioneer.log
    export PATH=$JAVA_HOME/bin:$PATH
else
    echo "Java is not found so installing JRE now" >> /tmp/salsa.pioneer.log
    sudo apt-get -q update
    sudo apt-get -q -y install openjdk-7-jre-headless wget
fi

# Check for wget
if type -p wget; then
    echo found wget executable in PATH
else
    sudo apt-get -q update
    sudo apt-get -q -y install wget
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

# export to use right now the Working dir
SALSA_WORKING_DIR=$SALSA_WORKING_DIR/$DOCKER_NODE_ID.$DOCKER_INSTANCE_ID
export SALSA_WORKING_DIR

mkdir -p $SALSA_WORKING_DIR
mv -f $TMPFILE $SALSA_WORKING_DIR/salsa.variables
cd $SALSA_WORKING_DIR

wget -q $SALSA_PIONEER_WEB/$SALSA_PIONEER_RUN

java -jar salsa-pioneer.jar startserver
