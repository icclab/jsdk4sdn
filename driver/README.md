Controller Driver API
=====================

The Driver API specifies the JSON data format as well as the function that will be abstracted to the SDK4SDN. The API is structured by the release plan. The first version of the driver specification contains the needed messages and events for the simple switch application. Later specification will also contain further events like switch enter. This events are more high-level then the ones specified in OpenFlow.

The driver is implemented in the controller as SDN application or SDN module, depending on the controller where the driver is implemented. The basic structure of the driver object looks as follows:

`Note:` The following class is pseudo code. It does not say that the class needs to be implemented exactly like this. It depends on the implementation of the SDN controller itself where and how it is implemented as well as on the used programming language. It could be further that this needs to be splitted into multiple classes/objects.

```python
	 class Driver(object):
		"""
		This is a skeleton class definition in pseudo code for a implementation
		of the driver for the SDK4SDN in a controller (RYU, NOX, Floodlight...). 
		"""
		def __init__(self, *args, **kwargs):
			"""
			The constructor should initialize the zmq publisher and subscriber
			socket. This is the connection to the SDK4SDN. Since all messages 
			are going through a message queue, it does not matter what is 
			started first.
			"""
			self.pub = context.socket(zmq.PUB)
			self.sub = context.socket(zmq.SUB)
			
		def handle_packet_in():
			"""
			This is the packet in handler of the controller. It reactos on every
			packet in on the network. The event comming from the network is 
			serialized by this function and will be sent to the SDK4SDN.
			
			OpenFlow reference: OFPPacketIn
			"""
			self.pub.send(serialized_data)
			
		def handle_switch_feature():
			"""
			This is the handler whenever a switch comes up in the network the 
			first time or after a connection loss with the controller. The 
			switch will send a Hello request. The event comming from the network 
			is serialized by this function and will be sent to the SDK4SDN.
			
			OpenFlow reference: OFPSwitchFeatures
			"""
			self.pub.send(serialized_data)
			
	        def handle_topology():
			"""
			This is the handler whenever there is a change in the topology.
			"""
			
	        def handle_sdk4sdn():
			"""
			This is the handler for all incoming messages of the SDK4SDN. It
			passes the received data to the deserialisation and calls the 
			corresponding out_ function
			"""
			if(self.sub.recv() == 1):
			    data = self.sub.recv()
			    if(data.deserialize().method == "OFPFlowMod"):
				    self.out_flow_mod(data)
			    if(data.deserialize().method == "OFPPacketOut"):
				    self.out_packet_out(data)
			
		def out_flow_mod():
			"""
			This method sends a flow modification to the specified devices in
			the outgoing message. This method is called by the handle_sdk4sdn
			method.
			
			OpenFlow reference: OFPFlowMod
			"""
		
		def out_packet_out():
			"""
			This method sends a packet out through the specified devices. This 
			method is called by the handle_sdk4sdn method.
			
			OpenFlow reference: OFPPacketOut
			"""
			
		def serialize():
			"""
			This method serializes the outgoing message to the SDK4SDN by the 
			specified JSON definition of the SDK4SDN. All outgoing data must be
			serialized by this method.
			"""
			
		def deserialize():
			"""
			This method deserializes the incoming message from the SDK4SDN to 
			the internal data structure of the corresponding controller.
			"""
```

Serialization
-------------

This section specifies how the data structure must look like for incoming and outgoing traffic to the SDK4SDN. At the moment, there are only the messages described, needed by the simple switch application.

OFPSwitchFeatures
-----------------

This describes the structure of message that is send to the SDK4SDN wehnever a new device entered the SDN network the first time or does a reconnect.

```json
   {
	  "OFPSwitchFeatures": {
		 "capabilities": 79, 
		 "datapath_id": 9210263729383,
		 "n_tables": 255
	  }
   }
```

OFPPacketIn
-----------

This describes the structure of message that entered the controller via the OFPPacketIn event.

```json
   {
	  "OFPPacketIn": {
		 "buffer_id": 2, 
		 "cookie": 283686884868096, 
		 "data": "////////8gukffjqCAYAAQgABgQAAfILpH346goAAAEAAAAAAAAKAAAD", 
		 "match": {
			"OFPMatch": {
			   "oxm_fields": [
				  {
					 "OXMTlv": {
						"field": "in_port", 
						"value": 6
					 }
				  }, 
				  {
					 "OXMTlv": {
						"field": "eth_type", 
						"value": 2054
					 }
				  }, 
				  {
					 "OXMTlv": {
						"field": "eth_dst", 
						"value": "ff:ff:ff:ff:ff:ff"
					 }
				  }, 
				  {
					 "OXMTlv": {
						"field": "eth_src", 
						"value": "f2:0b:a4:7d:f8:ea"
					 }
				  }
			   ], 
			}
		 }, 
		 "reason": 1, 
		 "table_id": 1, 
		 "total_len": 42
	  }
   }
```

Simple Flow Modification
------------------------

This message is send by the SDK4SDN to the corresponding controller. Out of this message, the controller will compose a Flow Modification message and send it to the datapath.

```json
   {
	  "OFPFlowMod": {
		 "buffer_id": 2,
		 "datapath_id": 25,
		 "instructions": [
			{
			   "OFPInstructionActions": {
				  "actions": [
					 {
						"OFPActionOutput": {
						   "out_port": "FLOOD or port number"
						}
					 }
				  ],
			   }
			},
		 ], 
		 "match": {
			"OFPMatch": {
			   "length": 14, 
			   "oxm_fields": [
				  {
					 "OXMTlv": {
						"field": "eth_dst",
						"value": "f2:0b:a4:7d:f8:ea"
					 }
				  }
			   ], 
			}
		 }, 
		 "priority": 123, 
		 "table_id": 1
	  }
   }
```

Simple Packet out
-----------------

This message is send by the SDK4SDN to the corresponding controller. Out of this message, the controller perform a packet out action through a datapath.

```json
   {
	  "OFPPacketOut": {
		 "datapath_id": 25,
		 "actions": [
			{
			   "OFPActionOutput": {
				   "out_port": "FLOOD or port number"
			   }
			}
		 ], 
		 "buffer_id": 4294967295, 
		 "data": "8guk0D9w8", 
		 "in_port": 4294967293
	  }
   }
```
