## Distributed Resource Management for IoT Cloud Systems (may it have a name?)

**What:** This framework enables user to discover, connect and resource the information of IoT resources and the underlying network. It aims to a high level view of the IoT cloud system, provides extensibile architecture to integrate different information models, and supports distributed management by loose-couple communication protocol via message queue.

**User:**

 Software agent (edge/cloud app) or administrator can take advantage of the tool. An API and a client-utility will be provided.

**Feature:**

1. Information of IoT resources and network is abstracted by means of two main entities:
  * Software-defined gateway: give information of data, control and connectivity, reflect IoT devices connecting to the gateway. E.g. the information about data from many sensors transfering through the gateway.
  * Virtual router: give information about the network link. In support virtual router, the higher information about network graph, and end-to-end network service can be generate.
  
*Status:* We have base model entities. TODO: extend the details of the model to cover more domain-specific information

2. Query management: users can query only needed information. E.g.: to get SD Gateway and its capabilities list related to camera in such a location/building. 

*Status:* How the query look like? Should we reuse a language like SPARQL (it supports RDF, thus we need to upgrade the implementation of the model), but I don't know how yet, need sometimes to check.

3. Distributed communication: the framework suppports both broadcast and unicast message, both synchronous (RPC call) and asynchorous (public/subscribe) communication.

*Status:* Having the communication management. More message can be added quickly to the protocol.

4. Integrate more adaptor. To implement new adaptors, two mechanism is need: (1)an information collector and validation, and (2)an information transformer to map the information

*Status:* Having base interface of the Collect&Validation and the Transformer. Testing on three information source: Android sensors(API/file-based), OpenIOT service (API/file-based), weave-virtual-router (commandline output)


**Usecase**

The flow of using the framework as following:
1. User deploy the local resource management on the gateway, then configure the type of resources.
2. User deploy the local resource management on cloud and configure some other providers.
3. User run the global resource management on the cloud or laptop, which communication with other local resource management.
4. User can execute a query from the client, which will be send to the queue message. Depending on the query, the message will be sent to:
  * Global resource management: to get the topology of the network, the relationship between component. Basically, the response will be a graph.
  * Local resource managemet: to get the detail information and capability of particular component.

![Architecture](https://raw.githubusercontent.com/tuwiendsg/SALSA/master/information-management/architecture.png "The architecture of the tools")


