#!/bin/bash

echo "Script for running data market agences"

wget -N http://10.99.0.14/salsa/upload/files/datamarket/agence.jar

DATABUS_IP=10.99.0.14
ID=`echo $RANDOM`
PROCESS_LIST="process_list.txt"

screen -d -m -S socket java -jar agence.jar -u vu -p 123 -i $ID -m subscribe -t http://traffic.hcmut.edu.vn/service/stream1 -h $DATABUS_IP -f $ID.txt -l yes
PID=`echo $$`
echo $PID >> $PROCESS_LIST


