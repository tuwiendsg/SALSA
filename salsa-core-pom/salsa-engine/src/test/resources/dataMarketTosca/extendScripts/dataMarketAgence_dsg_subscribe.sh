#!/bin/bash

echo "Script for running data market agences"

#wget -N http://10.99.0.14/salsa/upload/files/datamarket/agence.jar

export DATABUS_IP=10.99.0.14
export ID=`echo $RANDOM`

export TOPIC=$1
export ID=topic_$1_$ID

VAR=$(expect -c '
        set timeout 300
        send_user " ---> test\r"
        set ID $env(ID)
		set TOPIC $env(TOPIC)
        set DATABUS_IP $env(DATABUS_IP)
        send_user "$DATABUS_IP -- $ID \r"        
        spawn java -jar agence.jar -u tester -p 123 -i $ID -m subscribe -t http://www.test.com/test/stream$TOPIC -h $DATABUS_IP -f $ID.txt -l yes
        expect {
				timeout { send "yo \r \n" }
        }
')

echo $VAR


