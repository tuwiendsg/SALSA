## Distributed Resource Management for IoT Cloud Systems (name it D-Elise)


**Target users:**  Software agents (edge/cloud app), global management services (resource provisioner) and developers/administrators. 

**Features:**

1. Capture information of IoT and network resources is abstracted by means of two main entities:
  * Software-defined gateway: give information of data, control and connectivity, reflect IoT devices connecting to the gateway. E.g. the information about data from many sensors transfering through the gateway.
  * Virtual router: give information about the network link. In support virtual router, the information about network graph, and end-to-end network service can be generated.
  
*Status:* We have base model entities, we can extend the details of the model to cover more domain-specific information.

*Issues:* How to abstract the low level resources (sensors, actuators) into higher level: data point, control point? Currently the model uses simple mapping mechanism.

2. Query management: users can query only needed information. E.g.: to query SD Gateways and their capabilities about temperature data/control in such a location/building. User can use one-time query or subscription to get changes.

*Status:* We have protocol for both query types. But the query must be improved based on the model.

*Issues*: How the query look like? Should we reuse a language like SPARQL (it supports RDF, thus we need to upgrade the implementation of the model). This is not developed yet.

3. Distributed communication: the framework suppports both broadcast and unicast message, both synchronous (RPC call) and asynchorous (public/subscribe) communication. List of message see at the end of the document.

*Status:* Having the communication management. More message can be added quickly to the protocol.

4. Integrate more adaptor. To implement new adaptors, two mechanism is need: (1)an information collector and validation, and (2)an information transformer to map the information

*Status:* Having base interface of the Collect&Validation and the Transformer. Testing on three information source: Android sensors(API/file-based), OpenIOT service (API/file-based), weave-virtual-router (commandline output)

5. Capture the relationships. User can query at the global resource management, receive the list of relationships between component. Base on the type of relationship, we can form a graph between resources. The graph can be drawn by D3JS (java script, generate on web, enable interaction) or graphviz (static, simple then faster implemented)

**Issue:** Do we really need the graph. The software-based user do not need graph. Human-based user can use the graph to have an overview. Anyway, the information can only be retrieved via commandline.

**Usecase**

The flow of usage the framework as following:
1. User deploy the local resource management on the gateway, then configure the type of resources.
2. User deploy the local resource management on cloud and configure some other providers.
3. User run the global resource management on the cloud or laptop, which communication with other local resource management.
4. User can execute a query from the client, which will be send to the queue message. Depending on the query, the message will be sent to:
  * Global resource management: to get the topology of the network, the relationship between component. Basically, the response will be a graph.
  * Local resource managemet: to get the detail information and capability of particular component.

![Architecture](https://raw.githubusercontent.com/tuwiendsg/SALSA/master/information-management/architecture.png "The architecture of the tools")


Apendix:
1. The list of the message 
```java
        // broadcast: client->local/global: a syn message to check status of local/global managements
        SYN_REQUEST,
        SYN_REPLY,        
        // unicast: Client->local, query information from local regarding to SD Gateway or NVF
        RPC_QUERY_SDGATEWAY_LOCAL,
        RPC_QUERY_NFV_LOCAL,
        // unicast: Client->global, query information from global (which include relationship)
        RPC_QUERY_INFORMATION_GLOBAL,
        // unicast: Client->local, send a control command to local
        RPC_CONTROL_LOCAL,
        // unicast: Client->global, send a control command to global
        RPC_CONTROL_GLOBAL,
        // unicast: local/global --> client: send back the response
        UPDATE_INFORMATION
```
