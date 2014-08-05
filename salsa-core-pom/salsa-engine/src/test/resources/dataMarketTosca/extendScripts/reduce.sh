#!/bin/bash

IPLIST=( 10.99.0.32 10.99.0.65 10.99.0.66 10.99.0.35 10.99.0.40 )
ZIPLIST=""

EXP=3
INST=1

for i in "${IPLIST[@]}"
do
        ssh -i /root/hung-key.pem ubuntu@$i "sudo bash /root/zipResults.sh $EXP $INST"
        scp -i /root/hung-key.pem ubuntu@$i:/tmp/exp$EXP-$INST.zip .
        ZIPLIST="$ZIPLIST exp$EXP-$INST.zip"
        INST=`expr $INST + 1`
done

zipmerge exp$EXP-sub.zip $ZIPLIST
rm $ZIPLIST
