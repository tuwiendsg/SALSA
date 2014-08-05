#!/bin/bash

EXP=$1
INST=$2

cd /root

zip exp$EXP-$INST.zip *log*
mv exp$EXP-$INST.zip /tmp
