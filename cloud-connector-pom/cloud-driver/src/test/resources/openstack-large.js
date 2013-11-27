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
        "image-id" : "ami-00000225",
        "instance-type" : "m1.large",
    	"security-groups" : ["default"]
    }
}