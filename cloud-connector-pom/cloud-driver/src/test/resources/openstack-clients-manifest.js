{
    "roles-starup" : [ "clients" ],
    "clients" : {
        "role-id" : "clients", 
        "role-description" : "JMeter Clients",
        "role-version" : "1.0",
        "min-instance-count" : 1,
        "max-instance-count" : 1,
        "initial-instance-count" : 1,
        "image-id" : "ami-0000014e",
        "instance-type" : "m1.small",
    	"security-groups" : ["default"], 
        "ssh-key-name" : "alessiogambi",
        "start-up" : ["controlinterface","jmeter"],
		"userdata" : {
    		"jmeter" : {
	        	"env" : {
                    "jmx" : {
                        "url" : "@jmxURL"
                    },
                    "trace" : {
                        "url" : "@traceURL"
                    }
                },
                "notify" : {
                   "url" : "http://@joperaIp:@joperaPort/experiment/@experimentId/results"
                },
                "startup" : {
                    "J" : {
                        "loadbalancer" : {
                            "ip" : "@@UUID@entrypoint-ip",
                            "port" : 8080
                        },
                        "clients" : {
                            "number" : 20
                        },
                        "initial" : {
                        	"delay" : 60000,
                            "unit" : "millis"
                        },
                        "experiment" : {
                            "id" : "@experimentId"
                        },
                        "jopera" : {
                            "ip" : "@joperaIp",
                            "port" : "@joperaPort"
                        }
                    }
                },
                "publish" : {
                    "to" : {
                        "ip" : "@memcachedIp",
                        "port": "@memcachedPort"
                    },
                    "objectKey" : "@experimentId-clientResults" 
                }
	    	}
        }
	}
}