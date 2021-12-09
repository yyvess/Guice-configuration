Guice configuration module, JSON, HOCON & Properties formats supported, build on the top of Typesafe config

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.jmob/guice.conf/badge.svg?gav=true)](https://maven-badges.herokuapp.com/maven-central/net.jmob/guice.conf) [![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

[![Sonar Status](https://sonarcloud.io/api/project_badges/measure?project=net.jmob%3Aguice.conf&metric=alert_status)](https://sonarcloud.io/dashboard?id=net.jmob%3Aguice.conf)  [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.jmob%3Aguice.conf&metric=coverage)](https://sonarcloud.io/dashboard?id=net.jmob%3Aguice.conf)


Guice configuration
======

## Overview

- Guice injection 
- JSON, HOCON and Properties formats
- Substitutions ${foo.bar}
- Validation

## Binary Releases

You can find published releases on Maven Central.

		<dependency>
			<groupId>net.jmob</groupId>
			<artifactId>guice.conf</artifactId>
			<version>v1.5.0</version>
		</dependency>
		
Optionally, to active validation, you must import a validator like Hibernate validator
		
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator-cdi</artifactId>
            <version>6.2.0.Final</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>javax.el</artifactId>
            <version>3.0.0</version>
        </dependency>

Link for direct download if you don't use a dependency manager:

 - https://mvnrepository.com/artifact/net.jmob/guice.conf
 
 
## Quickstart

A configuration file `app.json` :

```javascript
{
  "port": 8080,
  "complexEntries": {
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

An interface where inject your structured configuration

```java  
   public interface MyServiceConfiguration {

      @Length(min = 5)
      String getHostname();
    
      Map<String, String> getAMap();
    
      List<String> getAList();
   }
```

A service where configuration should be inject
```java  
    @BindConfig(value = "app", syntax = JSON)
    public class Service {

        @InjectConfig
        private Optional<Integer> port;

        @InjectConfig("complexEntries")
        private MyServiceConfiguration config;

        public int getPort() {
            return port.orElse(0);
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
            install(new ConfigurationModule());
            requestInjection(Service.class);
        }
    }
```

Configuration files are loaded of classpath by default

A directory can be specified to load configuration outside of classpath

```java  
    public class GuiceModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new ConfigurationModule()
                .fromPath(new File("/etc")));
            requestInjection(Service.class);
        }
    }
```


Variables on your configuration file can be substitued with environment variables.

Substitution should be active with the option 'resolve'

```java  
@BindConfig(value = "config, resolve = true)
```

```javascript
{
  myconfig: ${my.environement.property}
}
```


Please find more examples on src/test/samples

## Supported types

- boolean, Boolean
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
