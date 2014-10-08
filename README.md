Installation

To build the SDK4SDN from source, you need ZMQ. The SDK4SDN itself is build 
with maven. The following instructions were executed on a freshly installed 
ubuntu 14.04 server. Prepare the system by installing the packages:

# apt-get update
# apt-get upgrade
# apt-get install maven gcc g++ git cmake libtool automake autoconf pkg-config openjdk-7-jdk

Get zeromq and jzmq and build them like this:

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
# ./configure
# make
# make install

Now install the SDK4SDN as follows

# cd /usr/local/src/