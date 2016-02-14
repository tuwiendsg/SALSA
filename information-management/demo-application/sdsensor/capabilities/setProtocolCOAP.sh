. ./capabilities/init.sh

PROTOCOL_CONF='<bean id="producer" class="coapClient.CoapMock" />'

sed -i 's#<bean id="producer" class=".*" />#<bean id="producer" class="coapClient.CoapMock" />#' $CONFIG_BASE/wire.xml

./capabilities/stop.sh
./capabilities/start.sh

