package net.jmob.guice.conf.core.samples.simple;

import static java.lang.String.format;

import java.util.Optional;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;

@BindConfig(value = "net/jmob/guice/conf/core/samples/sample_01")
public class MyApp extends AbstractModule {

    @InjectConfig
    private Optional<Integer> port;

    @InjectConfig("complexType")
    private ServiceConfiguration config;


    @Override
    protected void configure() {
        install(ConfigurationModule.create());
    }

    public static void main(String... args)  {
        Injector injector = Guice.createInjector(new MyApp());
        MyApp myApp = injector.getInstance(MyApp.class);
        System.out.println(format("Service value : %s, port %d", myApp.config.getValue(), myApp.port.orElse(-1)));
    }
}
