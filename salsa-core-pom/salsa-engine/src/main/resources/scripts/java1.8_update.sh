if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME     
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
fi

REQUIRE_JAVA=1.8.0_51

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" > "$REQUIRE_JAVA" ]]; then
        echo version is more than $REQUIRE_JAVA, it will be used
    else         
        echo version is less than $REQUIRE_JAVA, attempt to install new jre
    fi
fi

cd /opt

wget -q --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-linux-x64.tar.gz"
tar -xzf jre-8u60-linux-x64.tar.gz
cd jre1.8.0_60/

update-alternatives --install /usr/bin/java java /opt/jre1.8.0_60/bin/java 100
sudo update-alternatives --set java /opt/jre1.8.0_60/bin/java

export JRE_HOME=/opt/jre1.8.0_60
export PATH=$PATH:/opt/java/jdk1.8.0_45/bin:/opt/java/jdk1.8.0_45/jre/bin