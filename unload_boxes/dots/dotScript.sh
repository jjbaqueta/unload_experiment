#!/bin/bash

files=`ls ./*.dot`

for eachfile in $files
do
    dot -Tpng -o "${eachfile%.*}.png" $eachfile
done
