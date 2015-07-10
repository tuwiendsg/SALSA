if type -p java; then
    echo "Found java executable in PATH" | tee /tmp/salsa.pioneer.log
    which java | tee /tmp/salsa.pioneer.log
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Found java executable in JAVA_HOME" | tee /tmp/salsa.pioneer.log
    export PATH=$JAVA_HOME/bin:$PATH
else
    echo "Java is not found so installing JRE now" | tee /tmp/salsa.pioneer.log
    sudo apt-get -q update | tee /tmp/salsa.pioneer.log
    sudo apt-get -q -y install openjdk-7-jre-headless wget | tee /tmp/salsa.pioneer.log
fi

# Check for wget
if type -p wget; then
    echo "Found wget executable in $PATH"
else
    echo "Not found wget, install now"
    sudo apt-get -q update
    sudo apt-get -q -y install wget
fi