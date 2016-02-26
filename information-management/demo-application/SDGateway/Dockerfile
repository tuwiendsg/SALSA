FROM leduchung/ubuntu:14.04-jre8

RUN mkdir /tmp/delise
ADD ./run_delise.sh /tmp/delise/run_delise.sh
ADD ./collector-1.0.jar /tmp/delise/collector-1.0.jar
ADD ./TranformSDSensor-1.0.jar /tmp/delise/TranformSDSensor-1.0.jar

CMD ["/bin/sh", "/tmp/delise/run_delise.sh"]
