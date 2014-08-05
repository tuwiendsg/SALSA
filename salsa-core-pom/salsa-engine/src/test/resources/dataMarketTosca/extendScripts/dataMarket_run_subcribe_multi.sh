#!/bin/bash

apt-get -y install openjdk-7-jre expect ntp
wget -N http://128.130.172.215/salsa/upload/files/datamarket/scripts/dataMarketAgence_dsg_subscribe.sh
wget -N http://10.99.0.14/salsa/upload/files/datamarket/agence.jar

NUM_TOPIC=10
NUM_RUN=5

SCRIPT=dataMarketAgence_dsg_subscribe.sh

for (( c=1; c<=$NUM_TOPIC; c++ ))
do
    echo "Run topic: $c"
	#mkdir topic_$c
	#cp $SCRIPT topic_$c
	#cd topic_$c

    # run 5 time for 1 topic
	for (( i=1; i<=$NUM_RUN; i++ ))
	do
    	nohup /bin/bash $SCRIPT $c &	
		echo "Running topic $c time $i"
	done 
	#cd ..
done
