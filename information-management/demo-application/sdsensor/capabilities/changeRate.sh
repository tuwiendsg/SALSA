. ./capabilities/init.sh

echo "CONFIG_BASE=$CONFIG_BASE"

if [ -n "$1" -a "$1" != " " ]
then
  NEW_VAL=$1
  sed -i '/<property name="updateRate" value="[0-9]*"\/>/c\<property name="updateRate" value="'$NEW_VAL'"\/>' $CONFIG_BASE/wire.xml
  
  sed -i 's#rate=.*#rate='$NEW_VAL'#' $BASE/sensor.meta
  
  echo "Update rate set to $NEW_VAL seconds!"
else
  echo "No update rate provided!"
fi

./capabilities/stop.sh
./capabilities/start.sh

