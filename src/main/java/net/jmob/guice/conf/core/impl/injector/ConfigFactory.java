package net.jmob.guice.conf.core.impl.injector;

import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

import net.jmob.guice.conf.core.BindConfig;

final class ConfigFactory {

    Config getConfig(ConfigParseOptions parseOptions, BindConfig bindConfig) {
        Config config = parseResourcesAnySyntax(bindConfig.value(), parseOptions);
        if (!bindConfig.path().isEmpty()) {
            config = config.getConfig(bindConfig.path());
        }
        if (bindConfig.resolve()) {
            config = config.resolve();
        }
        return config;
    }
}
