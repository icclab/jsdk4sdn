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
