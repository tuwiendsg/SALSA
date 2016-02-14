#!/bin/bash
# This script is used for building custom simulated sensors/gateways that compatible with iCOMOT platform
# The util will generate the sensors or gateways artifacts which is ready-to-use in iCOMOT
# usage: ./SensorGatewayUtil.sh <sensor|gateway> [Options|sensorOptions|gatewayOptions]

# Change: The script generate sensor to run at the relative folder (not at /tmp) as previous version.
#         This change enable SALSA to manage multiple sensor on the same container or VM

BINDIR=`pwd`
TARGET="iCOMOT-simulated-devices"
PDIR=$TARGET/protected

function printCopyright() {
	echo -e "SensorGatewayUtil.sh version 0.1"
  echo -e "Copyright 2015 - Distributed Systems Group, Vienna University of Technology"
	echo -e "  "
}

function printHelp() {				
        echo -e "Usage : $0 <sensor|gateway> [Options|SensorOptions|GatewayOptions]"
        echo -e "   sensor|gateway : to create sensor artifact or gateway artifact"
				echo -e "   Options "
        echo -e "     -i|--interactive                 : Run the utility in interactive mode"
        echo -e "     -d|--delete <sensor/gateway name>: Delete a sensor or gateway created by this utility"
        echo -e "     -r|--remote <iCOMOT-repository>  : If stated, the artifact will be uploaded to iCOMOT-repository"
        echo -e "     -n|--name <sensor/gateway name>  : The name of sensor or gateway"
				echo -e "     -h|--help                        : Print this message and exit"
        echo -e "   SensorOptions"
        echo -e "     -s|--dataset <data set URI>      : The source of data set (can be URL or local file)"
        echo -e "                                        Default: searching for ./data.csv"
        echo -e "     -l|--maxlines <max lines>        : The maximum number of lines for the simulated data"
        echo -e "     -c|--columns <list of columns>   : The columns of dataset to attract. E.g: -c 1,3,4"
        echo -e "     -p|--protocol <sensor protocol>  : Set the sensor protocol. Supported: dry, mqtt, coap, smap"
        echo -e "     -f|--frequency <frequency>       : Set the sensor frequency (in seconds)"
        echo -e "   GatewayOptions  "
        echo -e "     -bi|--baseImage <Dockefile URL>  : The Dockerfile that configures the components of the gateway"
        echo -e "                                        Default: searching for ./Dockerfile"
        echo -e "     -ng|--noGovOps                   : Do not include rtGovOpt components"
        echo -e " "        
}

errorMsg="unknown"
function printError() {
	echo -e "\e[7mError: $errorMsg \e[0m"
	printHelp
	exit 1
}


function createSensor(){
  TARGET=$TARGET/sensors
	echo "Generating sensor code ..."
	mkdir -p $TARGET/$NAME
	mkdir -p $PDIR
	#Add README
	touch $PDIR/README
	echo "This directory is created automatically by iCOMOT. Please do not change its content manually..." > $PDIR/README

	echo "Gathering artifacts..."
	SCRIPTFILE=$(readlink -f "$0")
	SCRIPT=$(dirname "$SCRIPTFILE")
	
	if [ -f $SCRIPT/../examples/sensors/sensor.tar.gz ]; then
	  echo "sensor.tar.gz is found in \"examples\" folder!"
		cp ../examples/sensors/sensor.tar.gz $PDIR
	elif [ -f ./sensor.tar.gz ]; then
	  echo "sensor.tar.gz is found in current folder!"
	  cp ./sensor.tar.gz $PDIR
	else
	  echo "The sensor.tar.gz is not found in current folder ($PWD). By default you can find it in ../examples/sensors/sensor.tar.gz"
	  read -p "Please enter the path to sensor.tar.gz, or press enter to download it: " SENSORTAR
	  while [ ! -f "$SENSORTAR" ] && [ ! -z "$SENSORTAR" ]
	  do
	    echo "File does not exist: $SENSORTAR."
	    read -p "Please enter the path to sensor.tar.gz, or press enter to download it: " SENSORTAR
	    if [ -f $SENSORTAR ]; then
	      cp  $SENSORTAR $PDIR
	    fi
	  done
	fi	
	
	if [ ! -f $PDIR/sensor.tar.gz ]; then
	  	wget -N https://github.com/tuwiendsg/iCOMOT/raw/master/examples/sensors/sensor.tar.gz -O $PDIR/sensor.tar.gz
	fi

  if [ $DATASET == ^http://.* ]; then
  	wget -N $DATASET
  	if [ $? -nq 0 ]; then
  		echo "Error: Cannot get dataset at URL: $DATASET"
  		exit 2
  	fi
  elif [ -f $DATASET ]; then
  	echo "Getting dataset at local host: $DATASET"
    cp $DATASET $TARGET/$NAME
  else
  	echo "Error: Cannot get dataset at local: $DATASET"
  	exit 2
  fi

	# Generate script to run the sensor
	DATASETNAMEONLY=`basename $DATASET | cut -d'.' -f1`
	FILE=$TARGET/$NAME/runSensor_$DATASETNAMEONLY'_'$NAME.sh

	# Generate the wire.xml string
	PROTOCOL_CONF='at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun'
	FREQUENCY_CONF="<property name=\"updateRate\" value=\"$FREQUENCY\"/>"
	case $PROTOCOL in
		dry)
			PROTOCOL_CONF='at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun'
		  ;;
		mqtt)
		  PROTOCOL_CONF='at.ac.tuwien.infosys.cloudconnectivity.mqtt.MQProducer'
		  ;;
		coap)
		  PROTOCOL_CONF='coapClient.CoapMock'
		  ;;
		smap)
		  PROTOCOL_CONF='smapClient.SmapMock'
		  ;;
		*)
	esac
	PROTOCOL_CONF="<bean id=\"producer\" class=\"$PROTOCOL_CONF\" />"

  # prepare sensor folder
  cp $PDIR/sensor.tar.gz $TARGET/$NAME
  cd $TARGET/$NAME
  tar -xvzf sensor.tar.gz
	rm sensor.tar.gz
	cd $BINDIR

	cat > $FILE << generatedScript
# This script is generated by $0 to run the custom sensor. The data is embbeded at the end of this file.
# This script can run standalone. For deploying it on gateway, please check the distribution version (in ./$NAME-distribution)
# The settings of the sensor is:
#    Dataset = $DATASET
#    Maxlines = $MAXLINES
#    Columns = $COLUMNS 
#    Protocol = $PROTOCOL 
#    Frequency = $FREQUENCY
# This script requires sensor.tar.gz in the same folder. If not, please uncomment following line.
# wget https://github.com/tuwiendsg/iCOMOT/raw/master/examples/sensors/sensor.tar.gz

# replace the data
sed '1,/^START OF DATA/d' \$0 > data.csv
mv data.csv config-files/data.csv

# configure the sensor in META-INF

sed -i 's#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />#$PROTOCOL_CONF#' config-files/META-INF/wire.xml
sed -i 's#<property name="updateRate" value=.*#$FREQUENCY_CONF#' config-files/META-INF/wire.xml

# run the sensor
#java -cp 'bootstrap_container-0.0.1-SNAPSHOT-jar-with-dependencies.jar:*' container.Main
#bash container_run.sh
cd ./capabilities
bash ./start.sh
exit 0

START OF DATA
generatedScript




# create distribution folder
mkdir -p $TARGET/$NAME/$NAME-distribution
cp $PDIR/sensor.tar.gz $TARGET/$NAME/$NAME-distribution

cat > $TARGET/$NAME/$NAME-distribution/`basename $FILE` << generatedScript
# This script is generated by $0 to run the custom sensor. The data is embbeded at the end of this file. 
# This script enables the sensor to run in a gateway, which is compatible with iCOMOT
# The settings of the sensor is:
#    Dataset = $DATASET
#    Maxlines = $MAXLINES
#    Columns = $COLUMNS 
#    Protocol = $PROTOCOL 
#    Frequency = $FREQUENCY
# This script requires sensor.tar.gz in the same folder. If not, please uncomment following line.
# wget https://github.com/tuwiendsg/iCOMOT/raw/master/examples/sensors/sensor.tar.gz

if [ ! -f ./sensor.tar.gz ]; then
	echo "Sensor artifact does not found!" | tee /tmp/sensor.err
  exit 1;
fi

CURRENT_DIR=\$(pwd)

# prepare sensor artifact for the iCOMOT-compatible gateway
tar -xvzf ./sensor.tar.gz
touch sensor.pid
chmod 777 sensor.pid
rm sensor.tar.gz

# replace the data
cd \$CURRENT_DIR
sed '1,/^START OF DATA/d' \$0 > data.csv
mv data.csv ./config-files/data.csv

# configure the sensor in META-INF
sed -i 's#<bean id="producer" class="at.ac.tuwien.infosys.cloudconnectivity.dryrun.Dryrun" />#$PROTOCOL_CONF#' config-files/META-INF/wire.xml
sed -i 's#<property name="updateRate" value=.*#$FREQUENCY_CONF#' config-files/META-INF/wire.xml

# With the distributed version, sensor is not started.
bash ./container_run_bg.sh
exit 0

START OF DATA
generatedScript


# add data to the end of the scripts. id is the data set, then the sensor name, afterward is the set of columns
	# add $ before each column number
  if [ -z "$COLUMNS" ]; then
    COL='$0'
  else 
    COL='$'`echo $COLUMNS | tr -d ' '`
    COL=`echo $COL | sed -e "s/,/,$/g"`
  fi

  # get the header
	head -1 $DATASET | awk -F',' -v OFS=',' '{print "id,name,"'$COL'}' >> $FILE

	# write the data to both scripts in TARGET folder and distribution folder
	DATASET_NAME_ONLY=`basename $DATASET | cut -d'.' -f1`
	if [ $MAXLINES -gt 0 ]; then
		tail --lines=+2 $DATASET | head -n $MAXLINES |  awk -F',' -v OFS=',' '{print "'$DATASET_NAME_ONLY','$NAME',"'$COL'}' | tee -a $FILE $TARGET/$NAME/$NAME-distribution/`basename $FILE` >/dev/null
	else
		tail --lines=+2 $DATASET | awk -F',' -v OFS=',' '{print "'$DATASET_NAME_ONLY','$NAME',"'$COL'}' | tee -a $FILE $TARGET/$NAME/$NAME-distribution/`basename $FILE` >/dev/null
	fi


  echo "The run script is generated: $FILE"
  echo "Sensor name: $NAME"
  echo "Dataset:   : $DATASET"
if [ $INTERACTIVE == "true" ]; then
  RUNME="Y";
  default=$RUNME;  read -p "Should I run the sensor now? [$RUNME]: " RUNME; RUNME=${RUNME:-$default}
  if [ $RUNME == "Y" ]; then
    echo "Running the sensor ..."
    cd $TARGET/$NAME
    chmod +x runSensor_$DATASETNAMEONLY'_'$NAME.sh
    bash runSensor_$DATASETNAMEONLY'_'$NAME.sh
  else
    echo "Successfully created sensor $NAME"
    echo "You can run the sensor with ./$FILE"
  fi
  cd ../..
fi
} # End create sensor

function gatherGatewayArtifacts(){
  # gathering artifacts into protected folder
  echo "Gathering gateway artifacts into protected folder"
  if [ -f ../examples/gateways/rtGovOps-agents.tar.gz ]; then
		cp ../examples/gateways/rtGovOps-agents.tar.gz $PDIR
	else 	
  	wget -N https://github.com/tuwiendsg/iCOMOT/raw/master/examples/gateways/rtGovOps-agents.tar.gz -O $PDIR/rtGovOps-agents.tar.gz
  fi
  if [ -f ../examples/gateways/decommission ]; then
		cp ../examples/gateways/decommission $PDIR
	else 	
  	wget -N https://raw.githubusercontent.com/tuwiendsg/iCOMOT/master/examples/gateways/decommission -O $PDIR/decommission
  fi
  
}

function createGateway(){
  TARGET=$TARGET/gateways
  echo "Generate gateway code ..."
	mkdir -p $TARGET/$NAME
	mkdir -p $PDIR
	#Add README
	touch $PDIR/README
	echo "This directory is created automatically by iCOMOT. Please do not change its content manually..." > $PDIR/README
  
  gatherGatewayArtifacts
  
  # generate the gateway file
  FILE=$TARGET/$NAME/Dockerfile
  
 	cat > $FILE << generatedDockerfile
FROM $GWBASEIMAGE
MAINTAINER iCOMOT
# This script is generated by $0 to run the custom gateway.
# The configuration of the gateway are
# GWBASEIMAGE: $GWBASEIMAGE
# GovOps: $WITHGOVOPS

generatedDockerfile

# add artifacts and start GovOps
if [ "$WITHGOVOPS" == "yes" ]; then
  cat >> $FILE << generatedDockerfile
# GovOps required services
# Add httpd (apache 2.2.29)
RUN apt-get update
RUN apt-get -y install apache2

ADD ./mapper.sh /usr/lib/cgi-bin/mapper
RUN chmod a+x /usr/lib/cgi-bin/mapper
  
#Add some test capabilities
COPY capabilities/ /usr/lib/cgi-bin/capabilities/
RUN chmod a+x -R /usr/lib/cgi-bin/capabilities/
RUN chmod 4777 /usr/lib/cgi-bin/capabilities/killJava.sh
RUN touch /usr/lib/cgi-bin/capabilities/log
RUN chmod a+rw /usr/lib/cgi-bin/capabilities/log

# Add deployment agent 
RUN mkdir /usr/share/provi-agent/
RUN chmod a+x /usr/share/provi-agent/
ADD ./agent.sh /usr/share/provi-agent/agent.sh
ADD ./profile.sh /usr/share/provi-agent/profile.sh
ADD ./starter_ubuntu.sh /usr/share/provi-agent/starter.sh
ADD ./decommission /bin/decommission

# Open ports 80 (REST server) 2812 (Monit) 5683 (CoAP Server)
EXPOSE 80 2812 5683

# add metadata and GovOps IP
RUN echo "GOVOPS_ENDPOINT=$GOVOPS_LBHOST_IP:$GOVOPS_LBHOST_PORT" >> /etc/environment
RUN echo "GATEWAY_META=\"$METASTRING\"" >> /etc/environment

# Start cron deamon and hold on to a process
RUN chmod +x /usr/share/provi-agent/starter.sh
CMD ["/bin/sh", "/usr/share/provi-agent/starter.sh"]

generatedDockerfile
fi

# unpack stuff into gateway folder
cp $PDIR/rtGovOps-agents.tar.gz $TARGET/$NAME
cd $TARGET/$NAME
tar -xzf rtGovOps-agents.tar.gz
rm rtGovOps-agents.tar.gz
cd $BINDIR

# prepare the distribution folder
mkdir -p $TARGET/$NAME/$NAME-distribution
cp $PDIR/rtGovOps-agents.tar.gz $TARGET/$NAME/$NAME-distribution
cp $PDIR/decommission $TARGET/$NAME/$NAME-distribution
cp $FILE $TARGET/$NAME/$NAME-distribution

  echo "The Dockerfile is generated: $FILE"
  echo "Gateway name: $NAME"
  echo "Base image  : $GWBASEIMAGE"
  if [ "$WITHGOVOPS" == "yes" ]; then
    echo "GovOps      : $GOVOPS_LBHOST_IP:$GOVOPS_LBHOST_PORT"
    echo "Metadata    : $METASTRING"
  fi
  

} # End create gateway

if [ $# -lt 1 ]; then
  errorMsg="Missing parameters..."
	printError
	exit 1
fi

if [ $1 == "sensor" ]; then
	METHOD="sensor"
elif [ $1 == "gateway" ]; then
  METHOD="gateway"
elif [ $1 == "-h" ] || [ $1 == "--help" ]; then
  printCopyright
	printHelp
	exit 0
else
  errorMsg="The first parameter is wrong, it can be either sensor or gateway"
  printError
  exit 1
fi

shift

# Get options
REMOTE="false"
REMOTE_URL=""
NAME=$METHOD
INTERACTIVE="false"

DATASET="./data.csv"
MAXLINES=0
COLUMNS=""
PROTOCOL="dry"
FREQUENCY="5"

GWBASEIMAGE="ubuntu:14.04"
WITHGOVOPS="yes"
GOVOPS_LBHOST_IP="128.130.172.199"
GOVOPS_LBHOST_PORT="8080"

while test $# -gt 0; do
	case "$1" in		
	# general options
		-r|--remote)
			REMOTE="true"
			REMOTE_URL=$2
			shift 2
			;;
		-n|--name)
		  NAME=$2
		  shift 2
		  ;;
		-i|--interactive)
		  INTERACTIVE="true"
		  shift
		  ;;
		-d|--delete)
		  NAME=$2
		  DELETE="true"
		  shift 2
		  ;;  
		  
	# sensor options
		-s|--dataset)
		  DATASET=$2
		  shift 2
		  ;;
		-l|--maxlines)
		  MAXLINES=$2
		  shift 2
		  ;;
		-c|--columns)
		  COLUMNS=$2
		  shift 2
		  ;;
		-p|--protocol)
		  PROTOCOL=$2
		  shift 2
		  ;;		  
		-f|--frequency)
		  FREQUENCY=$2
		  shift 2
		  ;;		  
  # gateway options
		-bi|--baseImage)
			GWBASEIMAGE=$2
			shift 2
			;;
		-ng|--noGovOps)
		  WITHGOVOPS="no"
		  shift
		  ;;		  		
		*)
			errorMsg="Wrong parameters"
			printError
			exit 1
    	break
      ;;
	esac
done

# interactive mode
if [ $INTERACTIVE == "true" ]; then
	echo "Interactive mode for creating $METHOD"
	read -p "Input a name for the sensor/gateway [$METHOD]: " NAME;   NAME=${NAME:-$METHOD}
	case $METHOD in
		sensor)
		  default=$DATASET;   read -p "Dataset [$DATASET]: " DATASET;                                    DATASET=${DATASET:-$default}
		  while [ ! -f $DATASET ] && [ dataset != ^http://.* ]; do
		  	echo "Cannot find the dataset file: $DATASET. The dataset must be exist locally or an URL to download. Please enter another!"
			  default=$DATASET;   read -p "Dataset [$DATASET]: " DATASET;                                    DATASET=${DATASET:-$default}
		  done		  
		  
		  default=$MAXLINES;  read -p "Maximum lines to extract from the dataset (0 for all lines) [$MAXLINES]: " MAXLINES; MAXLINES=${MAXLINES:-$default}
		  default=$COLUMNS;   read -p "Columns to extract from the dataset (e.g.: 2,3,4. Leave empty for all) []: " COLUMNS;         COLUMNS=${COLUMNS:-$default}
		  default=$PROTOCOL;  read -p "Sensor protocol [$PROTOCOL] (dry|mqtt|coap|smap): " PROTOCOL;              PROTOCOL=${PROTOCOL:-$default}
		  default=$FREQUENCY; read -p "Sensor frequency [$FREQUENCY]: " FREQUENCY;                                FREQUENCY=${FREQUENCY:-$default}
		  echo -e "\nCreating sensor with following settings: \n Dataset = $DATASET \n Maxlines = $MAXLINES \n Columns = $COLUMNS \n Protocol = $PROTOCOL \n Frequency = $FREQUENCY"			
			;;
		gateway)
  		GW_PRE_DOCKER=""
		  default=$GW_PRE_DOCKER; read -p "Enter the default Dockerfile if available, or press enter to continue [$GW_PRE_DOCKER]: " GW_PRE_DOCKER; GW_PRE_DOCKER=${GW_PRE_DOCKER:-$default}
		  if [ ! -z $GW_PRE_DOCKER ]; then
		  	echo "Custom Dockerfile is not supported yet. Please follow the next guildlines to build the gateway!"
		  fi
			default=$GWBASEIMAGE; read -p "Base image for the gateway (on Dockerhub) [$GWBASEIMAGE]: " GWBASEIMAGE; GWBASEIMAGE=${GWBASEIMAGE:-$default}
			default=$WITHGOVOPS; read -p "GovOps-enabled? [$WITHGOVOPS]: " WITHGOVOPS; 
			while [ "$WITHGOVOPS" != "no" ] && [ "$WITHGOVOPS" != "yes" ] && [ "$WITHGOVOPS" != "" ]; do
				echo "Wrong input: $WITHGOVOPS. Please answer \"yes\" or \"no\", or press return for the default settings."
				read -p "GovOps-enabled? [$WITHGOVOPS]: " WITHGOVOPS;
			done
			WITHGOVOPS=${WITHGOVOPS:-$default}
			if [ "$WITHGOVOPS" == "yes" ]; then
				default=$GOVOPS_LBHOST_IP; read -p "  GovOps IP [$GOVOPS_LBHOST_IP]: " GOVOPS_LBHOST_IP; GOVOPS_LBHOST_IP=${GOVOPS_LBHOST_IP:-$default}
				default=$GOVOPS_LBHOST_PORT; read -p "  GovOps Port [$GOVOPS_LBHOST_PORT]: " GOVOPS_LBHOST_PORT; GOVOPS_LBHOST_PORT=${GOVOPS_LBHOST_PORT:-$default}
			
				echo "Please enter the metadata for the gateway (Enter empty meta name to stop):"
				while [ "1" == "1" ]; do
					read -p "  Meta name:  " METANAME
					if [ ! -z "$METANAME" ]; then
						read -p "  Meta value: " METAVALUE
						if [ ! -z "$METAVALUE" ]; then
							METASTRING=$METASTRING"&$METANAME=$METAVALUE"
						else
							echo "Meta value cannot be empty"
						fi
					else
						# remove the first &
						if [ ! -z "$METASTRING" ]; then
							METASTRING=`echo $METASTRING | cut -c2-`
						fi
						break
					fi
				done # end while 1=1
			fi # end WITHGOVOPS==yes

			;;
		*)
		  errorMsg="Wrong method: $METHOD"
		  printError
		  exit 1
		  ;;
	esac
fi


if [ "$DELETE" == "true" ]; then
	rm -rf $TARGET/$METHOD's'/$NAME
	exit 0
fi


if [ "$METHOD" == "sensor" ]; then
  createSensor
elif [ "$METHOD" == "gateway" ]; then
  createGateway
fi










