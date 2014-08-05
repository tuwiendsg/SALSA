echo "Stop all agence.jar"

PROCESS_LIST="process_list.txt"

while read line
do
        echo "Stopping PID: $line"
        export ID=`echo $line`
        expect -c "spawn screen -d -m -S socket -r $ID -X stuff 'yo\n'; send 'yo\n'"
done < $PROCESS_LIST
