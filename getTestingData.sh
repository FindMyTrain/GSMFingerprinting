#! bin/bash

if [ ! -d Test ]; then
    mkdir Test
fi

cd Test
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
            cat sensorAll.csv >> Test/$operator".csv"
            #echo $files $(cat $files | grep Gps | wc -l)
            cd $trip
        done
    done
    cd ..
done

rm sensorAll.csv
