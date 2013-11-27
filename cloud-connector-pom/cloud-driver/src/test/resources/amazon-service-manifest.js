{
    "roles-starup" : [ "frontend", "backend" ],

    "frontend" : {
        "role-id" : "frontend", 
        "role-description" : "Front End Server",
        "role-version" : "1.0",
        "min-instance-count" : 1,
        "max-instance-count" : 1,
        "initial-instance-count" : 1,
        "image-id" : "ami-c30360aa",
        "instance-type" : "t1.micro",
        "ssh-key-name" : "starclusterkey",
    	"security-groups" : ["default"], 
    	"start-up" : ["controlinterface","monitoringendpoint","loadbalancer","monitoringgui"], 
		"userdata" : {
    		"monitoringendpoint" : {
	        	"startup" : {
    	        	"servicefqn" : "@serviceId",
        	    	"kpis" : "GET_POLLS_RC,CREATE_POLL_RC,GET_POLL_RC",
            		"listener" : "ch.usi.monitoring.aggregatiAggregatorListener"
        		}
	    	},
		    "loadbalancer" : {
    		    "startup" : {
        		    "port" : "8081"
        		}
        	},
			"monitoringgui" : {
        		"startup" : {
            		"port" : "8080",
	            	"servicefqn" : "@serviceId",
    	        	"system" : "#(privateIp)"
        		},
	        	"register" : {
		            "with" : "memcached",
    		        "ip" : "#(privateIp)",
        		    "port" : "8080",
	            	"on" : {
    	            	"role" : "service",
        	        	"ip" : "@memcachedIp",
            	    	"port" : "8181",
                		"experimentId" : "@experimentId"
	            	}
    	    	},
        		"publish" : {
            		"to" : {
                	"ip" : "@memcachedIp",
                		"port" : "@memcachedPort"
	            	},
    	        	"objectKey" : "@experimentId-serverResults"
        		}
        	}
        }	
	},

	"backend" : {
    	"role-id" : "backend", 
    	"role-description" : "Backend End Server",
    	"role-version" : "1.0",
    	"min-instance-count" : 1,
    	"max-instance-count" : 1,
    	"initial-instance-count" : 1,
    	
        "image-id" : "ami-c30360aa",
        "instance-type" : "t1.micro",
        "ssh-key-name" : "starclusterkey",
    	"security-groups" : ["default"], 
    	
    	"start-up" : ["controlinterface","doodledb"]
	}
}