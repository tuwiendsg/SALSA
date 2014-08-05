#!/bin/bash

DOCKER_NODE_ID=$1
DOCKER_INSTANCE_ID=$2

DOCKER_PIONEER_WEB=http://128.130.172.215/salsa/upload/files/pioneer/salsa-pioneer-vm-0.0.1-SNAPSHOT-executable.jar

. /etc/salsa.variables

echo \"Running the customization scripts\" 
#apt-get -q update
#apt-get -q -y install openjdk-7-jre wget

TMPFILE=salsa.variables.$DOCKER_NODE_ID.$DOCKER_INSTANCE_ID

echo "SALSA_SERVICE_ID=$SALSA_SERVICE_ID" > $TMPFILE
echo "SALSA_TOPOLOGY_ID=$SALSA_TOPOLOGY_ID" >> $TMPFILE
echo "SALSA_NODE_ID=$DOCKER_NODE_ID" >> $TMPFILE
echo "SALSA_REPLICA=$DOCKER_INSTANCE_ID" >> $TMPFILE

echo "SALSA_TOSCA_FILE=$SALSA_TOSCA_FILE" >> $TMPFILE
echo "SALSA_WORKING_DIR=$SALSA_WORKING_DIR" >> $TMPFILE
echo "SALSA_PIONEER_RUN=$SALSA_PIONEER_RUN" >> $TMPFILE
echo "SALSA_CENTER_ENDPOINT=$SALSA_CENTER_ENDPOINT" >> $TMPFILE

mv -f $TMPFILE /etc/salsa.variables

wget -q $DOCKER_PIONEER_WEB

java -jar salsa-pioneer-vm-0.0.1-SNAPSHOT-executable.jar deploy






