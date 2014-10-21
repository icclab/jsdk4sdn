Applications
------------

This directory conatinas the applications for the SDK4SDN. All applications are supposed to work with one controller that implements the SDK4SDN driver. This `README.md` will give you a brief overview of the currently implemented applications.

L2Domain
--------

This application shows, how a singel L2-Domain of multiple devices can be programmed with the SDK4SDN. It uses the `EventMainDatapath` event to do this.

L2Switch
--------

This is a simple demonstration application that can be used, if you just want to test some of the functionalities of the SDK4SDN. In other controllers, this application is usually known as the simple switch application.

TableMiss
---------

This application is used, if you want to install a TableMiss action in the first table of a device. It's not recommended, to disable this application.

Enabling/Disabling applications
-------------------------------

The file `disabled.txt` contains a list of java class names one per line. All class names listed in this file, are not loaded at the start of the SDK4SDN. Technicaly it's possible, to disable as well the system applications in the library of the SDK4SDN itself. Disable this applications only, if you know what you are doing.