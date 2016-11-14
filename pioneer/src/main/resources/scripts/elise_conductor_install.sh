#!/bin/bash
# This script is for install conductor
# input: URL to download conductor artifact

URL=$1
WORKING_DIR=.
FILE=elise-conductor.jar

if [ -z $URL ]
then
    echo "URL to download artifact is required"
    exit 1
fi

if [ -f $WORKING_DIR/$FILE ]
then 
    echo "File downloaded, quit"
    exit 0
fi

wget -N $URL -O $WORKING_DIR/$FILE

DOWNLOAD_RESULT=$?
if [ $DOWNLOAD_RESULT -ne 0 ]
then 
    exit $DOWNLOAD_RESULT
fi

mkdir -p $WORKING_DIR/extensions


