. ./capabilities/init.sh

PROTOCOL_CONF='<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.mqtt.MQProducer" />'

sed -i 's#<bean id="producer" class=".*" />#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.mqtt.MQProducer" />#' $CONFIG_BASE/wire.xml

sed -i 's#protocol=.*#protocol=mqtt#' $BASE/sensor.meta

./capabilities/stop.sh
./capabilities/start.sh

