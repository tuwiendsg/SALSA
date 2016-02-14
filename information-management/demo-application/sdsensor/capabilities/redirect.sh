# This script changes the variable in /etc/environment and restart the sensor
#  1. The reason is the Software-defined-Sensor is hard-coded to read from that system-wide file
#  2. As a consequence, all Sensor on the same host will be affected.
#  3. In the future, the sensor must support API to redirect it data flow

# Moreover, the /etc/environment has many lines, but the sensor read variable begin with "mqtt" only

if [ "$#" -eq 1 ]
then
  sed -i sed -i 's#mqtt.*=.*#mqtt_broker_IP='$1'#' /etc/environment
else
  echo "An endpoint to redirect is need!"
  touch redirect.err
fi

./capabilities/stop.sh
./capabilities/start.sh

