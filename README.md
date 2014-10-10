What is SDK4SDN?
----------------

The SDK4SDN is a framework for SDN programming with OpenFlow. The SDK4SDN needs 
a working SDN controller like RYU, OpenDaylight or trema.

Building and Installing SDK4SDN
-------------------------------

To build the SDK4SDN from source, you need ZMQ. The SDK4SDN itself is build 
with maven. The following instructions were executed on a freshly installed 
ubuntu 14.04 server. Prepare the system by installing the packages:

```bash
# apt-get update
# apt-get upgrade
# apt-get install maven gcc g++ git cmake libtool automake autoconf pkg-config openjdk-7-jdk
```

Get zeromq and jzmq and build them like this:

```bash
# cd /usr/local/src/
# git clone https://github.com/zeromq/zeromq4-x.git
# git clone https://github.com/zeromq/jzmq.git
# cd zeromq4-x
# ./autogen.sh
# ./configure
# make
# make install
# cd ../jzmq
# ./autogen.sh
# ./configure --prefix=/usr
# make
# make install
```

Now install the SDK4SDN as follows

```bash
# cd /usr/local/src/
# git clone gitor@dornbirn.zhaw.ch:software-defined-networking/jsdk4sdn.git
# cd jsdk4sdn/
# mvn clean install
```

Run the SDK4SDN

```bash
# cd /usr/local/src/jsdk4sdn
# mvn "-Dexec.args=-classpath %classpath sdk4sdn.Sdk4Sdn" -Dexec.executable=java org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
```

Running the full Stack
----------------------

For executing and working with the SDK4SDN, you need the following three major components:

* A running and compiled version of the SDK4SDN
* A SDN controller running the SDK4SDN driver (and any other SDN controller application)
* OpenFlow 1.3 enabled network devices or mininet - http://mininet.org/

This example here explains how to run a simple network topology with the SDK4SDN, Ryu SDN controller and mininet. You can create the full stack in 3 steps:

1. Install the SDK4SDN as described before in the section "Building and Installing SDK4SDN"
2. Install the Ryu controller with the corresponding SDK4SDN driver. The installation instructions can be found in this repository in the `driver/ryu` directory.
3. Create a mininet vritual machine with VirtualBox. The SDK4SDN runs at the moment only with OpenFlow 1.3. This means that you have to use (installed and compiled) the user switch of mininet https://github.com/CPqD/ofsoftswitch13

If you have problems installing mininet with the ofsoftswitch13, you can also download the ready made applience from here: [VBox mininet VM with ofsoftswitch13](https://owncloud.engineering.zhaw.ch/public.php?service=files&t=bb43c5b09b7d69322ba75e0d9c55e875 "mininet VM").

After everything is installed and setup, you can start the example. On your local machine, start the Ryu controller and the SDK4SDN. It does not matter, if you start first the SDK4SDN or the Ryu controller, they connect to each other with ZMQ sockets. Thus, both sides have a message queue.

```bash
localhost@terminal1 # cd /usr/local/src
localhost@terminal1 # mvn "-Dexec.args=-classpath %classpath sdk4sdn.Sdk4Sdn" -Dexec.executable=java org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
localhost@terminal2 # cd /usr/local/src/ryu
localhost@terminal2 # cd ryu-manager ryu/app/driver.py
```

Now start a simple topology in the virtual machine with mininet. For this VM, I always setup two network interfaces: The first one is a Host-Only adapter and the second one is NAT. The default gateway of the Host-Only adapter stays always at the IP 192.168.56.1. So every VM can connect via this IP to your localhost, no matter, what IP it's getting assigned by the VBox Host-Only adapter.

```bash
vm-mininet@terminal1 # mn -c
vm-mininet@terminal1 # mn --controller=remote,ip=192.168.56.1,port=6633 --switch=user
mininet> h1 ping h2
```

SDK4SDN driver
--------------

At the moment, there exist only one driver for the Ryu SDN Framework. Installation instructions are also in the corresponding driver directory. The following is a list of supported drivers (SDN controllers)

* Ryu - `driver/ryu`

Programming with the SDK4SDN
============================

This is basically the user guide. At the moment, the SDK4SDN supports a very limited set of the OpenFlow standard, but is expected, that the SDK4SDN will support the full OpenFlow 1.3 standard soon. Writing a SDN application with the SDK4SDN is not so hard and achieved by executing this 3 major steps.

1. Create a java class in the folder `src/main/java/app`
2. Programm the application
3. Run

Basic structure
---------------

The most basic version of your SDN application looks like this:

```java
package app;

import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class MySDNApp {
    public void MySDNApp(){}
}
```
Note the annotation `@Extension` to the class. This tells the SDK4SDN to load this class as a SDN application during compiling the controller.

Events
------

As long as you do not implement any events in your application, you will not receive OpenFlow messages. Implementing an event in the application does mean to implement the corresponding interface. If the application should react to OpenFlow packet_in events, you implement the interface `OFPEventPacketIn` like in the following example:

```java
package app;

import java.util.HashMap;
import ro.fortsoft.pf4j.Extension;
import sdk4sdn.Network;
import sdk4sdn.openflow13.*;

/**
 *
 * @author aepp
 */
@Extension
public class MySDNApp implements OFPEventPacketIn {
    public void MySDNApp(){}
    
    @Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
	}
}
```

OpenFlow API
------------

You can use the native API but it is recommended, to use the corresponding factory for creating and later working with OpenFlow messages. To create an OpenFlow packet out message, you can use the `OFPMessageFactory` like the following example explains:

```java
@Extension
public class MySDNApp implements OFPEventPacketIn {
    public void MySDNApp(){}
    ...
    @Override
	public void packetIn(OpenFlow OFPMessage, Network network) {
	OpenFlow message = new OpenFlow();
		//Create a packetOut message
		OFPPacketOut packetOut = OFPMessageFactory.CreatePacketOut(5, OFPMessage);
	
		//now compile the message
		message.setOFPPacketOut(packetOut);
		network.Send(message);
	}
	...
}
```

The example from above creates a PacketOut message that sends out the packet on port number 5. If you pass the incoming message as well to the `OFPMessageFactory`, many values are automatically set from the incoming message to the outgoing message. For instance, the packet is sent out through the same device, where it originally was send from. If you don't like this default behavior, omit the parameter OFPMessage to the factory.