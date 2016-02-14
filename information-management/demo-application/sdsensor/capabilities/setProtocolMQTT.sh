. ./capabilities/init.sh

PROTOCOL_CONF='<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.mqtt.MQProducer" />'

sed -i 's#<bean id="producer" class=".*" />#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.mqtt.MQProducer" />#' $CONFIG_BASE/wire.xml

./capabilities/stop.sh
./capabilities/start.sh

