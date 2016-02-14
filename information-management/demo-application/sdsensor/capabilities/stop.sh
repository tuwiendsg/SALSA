. ./capabilities/init.sh

echo "BASE is $BASE"

kill `cat $BASE/sensor.pid`
rm $BASE/sensor.pid
echo "Sensor stopped sucessfully!"

