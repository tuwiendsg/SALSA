{
    "roles-starup" : [ "frontend" ],
    "entry-point" : "frontend",
    "frontend" : {
        "role-id" : "frontend", 
        "role-description" : "Front End Server",
        "role-version" : "1.0",
        "min-instance-count" : 1,
        "max-instance-count" : 1,
        "initial-instance-count" : 1,
        "image-id" : "ami-0000014e",
        "instance-type" : "m1.small",
        "ssh-key-name" : "doodle-service",
    	"security-groups" : ["default"], 
    	"start-up" : ["controlinterface","monitoringgui"], 
		"userdata" : {
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
	}
}