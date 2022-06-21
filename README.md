<br/>


Eclipse Metro is a high-performance, extensible, easy-to-use web service stack.
It is a one-stop shop for all your web service needs, from the simplest
hello world web service to reliable, secured, and transacted web service
that involves .NET services. The Metro web service stack is a part of
the GlassFish community, but it can be also used outside GlassFish.

Eclipse Metro consists of [Eclipse Implementation of Jakarta XML Web Services](https://eclipse-ee4j.github.io/metro-jax-ws)
and Web Services Interoperability Technology project (WSIT). Eclipse Implementation of Jakarta XML Web Services
provides core web services support and the base framework for extensions provided by the WSIT layer.

Web Services Interoperability Technologies (WSIT) includes implementations of:

* WS-AtomicTransactions/Coordination
* WS-MetadataExchange
* WS-Policy
* WS-ReliableMessaging
* WS-SecureConversation
* WS-Security
* WS-SecurityPolicy
* WS-Trust
* SOAP over TCP


Note:

* WS-Policy (policy) has its [own codebase](https://github.com/eclipse-ee4j/metro-policy) however
WS-Policy related issues can be submitted into the Metro (WSIT) Issue tracker
* XML Web Services Security (xwss) project has been merged into the WSIT code base

This project is part of [Eclipse Metro](https://projects.eclipse.org/projects/ee4j.metro)


# <a name="Download_Metro_Release"></a>Download Eclipse Metro Release

The latest stable release of Eclipse Metro is available for
[download](https://repo1.maven.org/maven2/org/glassfish/metro/metro-standalone/3.0.0/metro-standalone-3.0.0.zip)
as well as for consumption through maven.
```
        <dependencies>
            <dependency>
                <groupId>org.glassfish.metro</groupId>
                <artifactId>webservices-api</artifactId>
                <version>3.0.0</version>
            </dependency>
        </dependencies>

        <dependencies>
            <dependency>
                <groupId>org.glassfish.metro</groupId>
                <artifactId>webservices-rt</artifactId>
                <version>3.0.0</version>
            </dependency>
        </dependencies>
```

## Documentation
The release includes the following documentation:
* [Getting Started](/3.0.0/getting-started/index.html)
* [Users Guide](/3.0.0/guide/index.html)
* [Change log](https://github.com/eclipse-ee4j/metro-wsit/releases/tag/3.0.0)


# <a name="Download_Archive"></a>Download previous versions

Previous versions of Eclipse Metro are available for download
as well as for consumption [through maven](https://repo1.maven.org/maven2/org/glassfish/metro/metro-standalone/).

## Documentation
The release includes the following documentation:
* [Getting Started](/2.4.4/getting-started/index.html)
* [Users Guide](/2.4.4/guide/index.html)
