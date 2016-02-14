. ./capabilities/init.sh

PROTOCOL_CONF='<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />'

sed -i 's#<bean id="producer" class=".*" />#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />#' $CONFIG_BASE/wire.xml

./capabilities/stop.sh
./capabilities/start.sh
