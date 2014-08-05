#!/bin/bash

echo "Script for running data market agences" 

wget -N http://10.99.0.14/salsa/upload/files/datamarket/agence.jar
wget -N http://10.99.0.14/salsa/upload/files/datamarket/agenceTxtData/test_data_20.tar.gz

tar -xzf test_data_20.tar.gz


#mkdir $ID
#cp agence.jar $ID
#cd $ID

DATABUS_IP=10.99.0.14
NUMBER=10


for (( c=1; c<=$NUMBER; c++ ))
do
		ID=`echo publish_topic_$c`
        java -jar agence.jar -u tester -p 123 -i $ID -m publish -t http://www.test.com/test/stream$c -h $DATABUS_IP -d 1 -f ./test_data -l yes &
done

