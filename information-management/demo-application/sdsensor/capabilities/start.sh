. ./capabilities/init.sh

echo "BASE is $BASE"
cd $BASE
./container_run_bg.sh
echo "Sensor started sucessfully!"
echo PID=`cat sensor.pid`

