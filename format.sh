#!/bin/bash

######################################################
###### $1 = Gps filename ### $2 = GSM filename #######
######################################################

cat $1 | grep Gps > sensors1.txt
cat $2 | grep cid > sensors2.txt
awk 'BEGIN{FS="[\t, ]"}{print $3 "," $7 "," $11 "," $13}' sensors1.txt > sensorGPS.csv
awk 'BEGIN{FS="[\t=, ]"}{if($8!="at"){print $2 "," $8 "," $10}}' sensors2.txt > sensorCID.csv
#awk 'BEGIN{FS="[\t=, ]"}{if($9!="at"){print $2 "," $9 "," $11}}' sensors2.txt > sensorCID.csv
javac Map.java
java Map > sensorAll.csv
rm sensors1.txt sensors2.txt sensorGPS.csv sensorCID.csv *.class
echo "Hey It's done !!!!"
