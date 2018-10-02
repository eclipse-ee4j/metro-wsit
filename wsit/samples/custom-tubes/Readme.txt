#
# Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

custom-tubes sample demonstrates the proprietary extension mechanism for injecting 
custom Tube implementations into the default Metro tubeline.

This sample requires Metro 2.1 or later.

- For integration of a custom tube into Metro, see the content of the etc/metro.xml 
configuration file.
- For a basic custom tube implementation see Java files under src/metro_sample/common
directory

After a successful execution of the sample, you should see a similar output:
- Server side (in the <glassfish-domain>/logs/server.log) :

[#|2011-02-01T12:17:11.030+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=19;_ThreadName=Thread-1;|Message request intercepted on Endpoint side|#]

[#|2011-02-01T12:17:11.037+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=19;_ThreadName=Thread-1;|Message response intercepted on Endpoint side|#]

[#|2011-02-01T12:17:11.058+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=20;_ThreadName=Thread-1;|Message request intercepted on Endpoint side|#]

[#|2011-02-01T12:17:11.058+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=20;_ThreadName=Thread-1;|Message response intercepted on Endpoint side|#]

[#|2011-02-01T12:17:11.069+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=21;_ThreadName=Thread-1;|Message request intercepted on Endpoint side|#]

[#|2011-02-01T12:17:11.070+0100|INFO|glassfish3.1|metro_sample.common.CustomTube|_ThreadID=21;_ThreadName=Thread-1;|Message response intercepted on Endpoint side|#]


- Client side (in the console):

Custom tubes sample application
===============================

Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTubeFactory createTube
INFO: Creating client-side interceptor tube
[ Client ]: Adding numbers 10 + 20
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processRequest
INFO: Message request intercepted on Client side
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processResponse
INFO: Message response intercepted on Client side
[ Client ]: Result as expected: 30


[ Client ]: Adding numbers 20 + 40
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processRequest
INFO: Message request intercepted on Client side
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processResponse
INFO: Message response intercepted on Client side
[ Client ]: Result as expected: 60


[ Client ]: Adding numbers 30 + 60
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processRequest
INFO: Message request intercepted on Client side
Feb 1, 2011 12:24:33 PM metro_sample.common.CustomTube processResponse
INFO: Message response intercepted on Client side
[ Client ]: Result as expected: 90


[ Client ]: Closing WS proxy...DONE.


Sample directory content:

* etc - configuration files
    * AddNumbers.wsdl wsdl file
    * deploy-targets.xml ant script to deploy the endpoint
      war file
    * metro.xml customized tubeline descriptor
    * sun-jaxws.xml deployment descriptor for web container
* src source files
    * metro_sample/client/AddNumbersClient.java - client application
    * metro_sample/server/AddNumberImpl.java - server implementation
    * metro_sample/common/*.java - custom tube and tube factory implementations

* wsimport ant task is run to compile etc/AddNumbers.wsdl
    * generates
      SEI - AddNumbersPortType
      service class - AddNumbersService
      and exception class - AddNumbersFault_Exception

* To run
    * set METRO_HOME to the Metro installation directory
    * ant clean server - runs wsimport to compile AddNumbers.wsdl and generate
      server side artifacts and does the deployment
    * ant clean client run - runs wsimport on the published wsdl by the deployed
      endpoint, compiles the generated artifacts and the client application
      then executes it.

* Prerequisites

Refer to the Prerequisites defined in samples/docs/index.html.

We appreciate your feedback, please send it to metro@javaee.groups.io.
