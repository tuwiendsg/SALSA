# this script is adhoc just for experiment

echo "BROKER=amqp://128.130.172.215" > salsa.variables
echo "BROKER_TYPE=amqp" >> salsa.variables

echo '' > info-source.conf

nohup java -cp "collector-1.0.jar:TransformSDSensor-1.0.jar" at.ac.tuwien.dsg.cloud.salsa.collector.Main &
