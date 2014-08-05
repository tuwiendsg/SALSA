#!/bin/bash

mkdir results
cp agence/*log* results

cd results

publish=publish.txt

for f in results/*.txt
do
        # read one file and publish's file
        while read -r -u 4 line1 && read -r -u 5 line2; do
                echo line1
                echo line2
        done 4<publish 5<f
done
