RYU driver for the SDK4SDN
--------------------------

This is the driver for RYU to communicate with the SDK4SDN.

Building and Installing RYU with the driver
-------------------------------------------

The driver in the Ryu controller also needs a localy compiled version of zmq. Please see the main page how to build zmq or http://zeromq.org/docs:source-git

```
# apt-get update
# apt-get upgrade
# apt-get install python-setuptools python-pip python-dev libxml2-dev libxslt1-dev zlib1g-dev
# pip install pyzmq simplejson
# pip install greenlet --upgrade
# pip install six --upgrade
# cd /usr/local/src
# git clone git://github.com/osrg/ryu.git
# cd ryu/
# python setup.py install
```

Copy the file driver.py from this repository to ryu's application folder and start the controller as follows:

```
# /usr/local/src/ryu
# ryu-manager ryu/app/driver.py
```

Supported SDK4SDN features
--------------------------

The following table represents a list of the currently supported features of the SDK4SDN in the RYU driver.

| SDK4SDN        | RYU driver           | Comment  |
| -------------  |-------------         | ----- |
| OFPPacketOut   | OFPPacketOut | only OFPActionOutput |
| OFPFlowMod     | x      |    |