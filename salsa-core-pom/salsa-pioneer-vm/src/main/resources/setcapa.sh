#/bin/bash
# Usage: setcapa.sh <capaId> <value>
. /etc/salsa.variables

if [ -z "$PIONEER_RUN" ]; then
	PIONEER_RUN=salsa-pioneer-vm-0.0.1-SNAPSHOT.jar
fi

if [ -z "$SALSA_WORKING_DIR" ]; then
	SALSA_WORKING_DIR=/opt/salsa
fi

java -jar $SALSA_WORKING_DIR/$PIONEER_RUN setcapa $1 $2


