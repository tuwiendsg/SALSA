#!/bin/bash

echo "Script for running data market agences" 

wget -N http://10.99.0.14/salsa/upload/files/datamarket/agence.jar
wget -N http://10.99.0.14/salsa/upload/files/datamarket/agenceTxtData/test_data_50.tar.gz

tar -xzf test_data_50.tar.gz

ID=`echo $RANDOM`

#mkdir $ID
#cp agence.jar $ID
#cd $ID

DATABUS_IP=10.99.0.14

java -jar agence.jar -u vu -p 123 -i $ID -m publish -t  http://traffic.hcmut.edu.vn/service/stream1 -h $DATABUS_IP -d 1 -f ./test_data -l yes

#rm agence.jar
