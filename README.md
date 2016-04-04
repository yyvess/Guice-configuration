Guice configuration module, JSON, HOCON & Properties formats supported, build on the top of 
Typesafe config

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.jmob/guice.conf/badge.svg)]
(https://maven-badges.herokuapp.com/maven-central/net.jmob/guice.conf)
[![Build Status](https://travis-ci.org/yyvess/gconf.svg?branch=master)](https://travis-ci.org/yyvess/gconf)

Guice-configuration
======

## Overview

- Guice injection 
- JSON, HOCON and Properties formats
- Validation

##Binary Releases

You can find published releases on Maven Central.

		<dependency>
			<groupId>net.jmob</groupId>
			<artifactId>guice.conf</artifactId>
			<version>1.2.0</version>
		</dependency>
		
Optionally, to active validation, you must import a validator like Hibernate validator
		
	    <dependency>
	      <groupId>org.hibernate</groupId>
	      <artifactId>hibernate-validator-cdi</artifactId>
	      <version>5.2.2.Final</version>
	    </dependency>
	    <dependency>
	      <groupId>javax.el</groupId>
	      <artifactId>javax.el-api</artifactId>
	      <version>2.2.4</version>
	    </dependency>

Link for direct download if you don't use a dependency manager:

 - http://central.maven.org/maven2/net/jmob/guice.conf/
 
 
## Quickstart

File `app.json` :

```javascript
{
  "port": 8080,
  "complexType": {
    "hostname": "www.github.com",
    "aMap": {
      "key1": "value1",
      "key2": "value2"
    },
    "aList": [
      "value1",
      "value2"
    ]
  }
}
```

Use a interface to inject complex data structure

```java  
   public interface ServiceConfiguration {

      @Length(min = 5)
      String getHostname();
    
      Map<String, String> getAMap();
    
      List<String> getAList();
   }
```

And inject it on a Service
```java  
    @BindConfig(value = "app", syntax = JSON)
    public class Service {

        @InjectConfig
        private Optional<Integer> port;

        @InjectConfig("complexType")
        private ServiceConfiguration config;

        public int getPort() {
            return port;
        }

        public ServiceConfiguration getConfig() {
            return config;
        }
    }
```

```java  
    public class GuiceModule extends AbstractModule {
        @Override
        protected void configure() {
            install(ConfigurationModule.create());
            requestInjection(Service.class);
        }
    }
```

More examples on src/test

## Supported types

- String
- int, Integer, double, Double
- List<?>, Map<?>, with typed value support
- Optional<?>
- Any Interface, a proxy of this interface is injected

## References

- https://github.com/typesafehub/config
- https://github.com/google/guice

## License

The license is Apache 2.0, see LICENSE file.

Copyright (c) 2015-2016, Yves Galante
