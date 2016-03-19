#! bin/bash

if [ ! -d Results ]; then
    mkdir Results
fi
javac FingerPrinting.java

for file in Airtel Vodafone Reliance Idea Aircel
do
    if [ -f Train/$file".csv" -a -f Test/$file".csv" ]; then
        echo "Lets find the error for "$file
        cp Train/$file".csv" train.csv
        cp Test/$file".csv" test.csv
        java FingerPrinting train.csv test.csv 
        cp error.txt Results/$file"_Error.txt"
        python plotCDF.py
        mv Graph_Loc.png Results/$file".png"
        rm train.csv test.csv error.txt output.csv
    fi
done
