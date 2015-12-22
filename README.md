Guice configuration module, JSON, HOCON & Properties formats supported, build on the top of 
Typesafe config

Guice-conf
======

Configure easily your applications

## Overview

- JSON, HOCON and Properties formats
- Guice injection 
- Validation

##Binary Releases

You can find published releases on Maven Central.

		<dependency>
			<groupId>net.jmob</groupId>
			<artifactId>guice.conf</artifactId>
			<version>1.0.0</version>
		</dependency>
		
To active validation, you must import a validator like Hibernate validator
		
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator-cdi</artifactId>
            <version>5.2.2.Final</version>
            <scope>test</scope>
        </dependency>

Link for direct download if you don't use a dependency manager:

 - http://central.maven.org/maven2/net/jmob/guice.conf/
 
 
## Quickstart

File `app.json` :

```javascript
{
  "port": 12,
  "complexType": {
    "value": "Hello World",
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
      String getValue();
    
      Map<String, String> getAMap();
    
      List<String> getAList();
   }
```

And inject it on a Service
```java  
    @BindConfig(value = "app", syntax = JSON)
    public class Service {

        @InjectConfig
        private int port;

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

## References

- https://github.com/typesafehub/config
- https://github.com/google/guice

## License

The license is Apache 2.0, see LICENSE file.

Copyright (c) 2015, Yves Galante

