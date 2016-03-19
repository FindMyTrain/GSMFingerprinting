#! bin/bash

if [ ! -d Train ]; then
    mkdir Train
fi

cd Train
for operator in Airtel Vodafone Idea Reliance Aircel
do
    echo "" > $operator".csv"
done
cd ..


for trip in $*
do
    cd $trip
    for operator in Airtel Vodafone Idea Reliance Aircel
    do
        for files in $operator*
        do
            cd ..
            echo $files
            ./format.sh $trip/GPS.txt $trip/$files
            cat sensorAll.csv >> Train/$operator".csv"
            #echo $files $(cat $files | grep Gps | wc -l)
            cd $trip
        done
    done
    cd ..
done

rm sensorAll.csv
