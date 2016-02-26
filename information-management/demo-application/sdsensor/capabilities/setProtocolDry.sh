. ./capabilities/init.sh

PROTOCOL_CONF='<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />'

sed -i 's#<bean id="producer" class=".*" />#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />#' $CONFIG_BASE/wire.xml

sed -i 's#protocol=.*#protocol=DRY#' $BASE/sensor.meta


./capabilities/stop.sh
./capabilities/start.sh
