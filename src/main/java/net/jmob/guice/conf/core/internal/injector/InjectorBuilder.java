/**
 * Copyright 2015 Yves Galante
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jmob.guice.conf.core.internal.injector;

import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.internal.ConfigFactory;
import net.jmob.guice.conf.core.internal.virtual.VirtualBeanFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.typesafe.config.ConfigParseOptions.defaults;
import static java.util.Arrays.stream;

@Singleton
public class InjectorBuilder {

    private final VirtualBeanFactory virtualBeanFactory;
    private final ConfigFactory configFactory;

    @Inject
    public InjectorBuilder(ConfigFactory configFactory, VirtualBeanFactory virtualBeanFactory) {
        this.configFactory = configFactory;
        this.virtualBeanFactory = virtualBeanFactory;
    }

    public Stream<Injector> build(Class beanClass) {
        final Config config = getConfig(configFactory, getOptions(beanClass), getAnnotationConfiguration(beanClass));
        return stream(beanClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(InjectConfig.class))
                .map(f -> virtualBeanFactory
                        .withConfig(config)
                        .withPath(getAnnotationPath(f))
                        .withType(f.getType())
                        .withField(f))
                .map(Injector::new);
    }


    private Config getConfig(ConfigFactory configFactory, ConfigParseOptions parseOptions, BindConfig bindConfig) {
        Config config = configFactory.parseResources(bindConfig.value(), parseOptions);
        if (!bindConfig.path().isEmpty()) {
            config = config.getConfig(bindConfig.path());
        }
        if (bindConfig.resolve()) {
            config = config.resolve();
        }
        return config;
    }

    private String getAnnotationPath(Field f) {
        final String annotationValue = f.getAnnotationsByType(InjectConfig.class)[0].value();
        return isNullOrEmpty(annotationValue) ? f.getName() : annotationValue;
    }

    private ConfigParseOptions getOptions(Class beanClass) {
        return defaults().setSyntax(ConfigSyntax.valueOf(getAnnotationConfiguration(beanClass).syntax().name()));
    }

    private BindConfig getAnnotationConfiguration(Class beanClass) {
        return (BindConfig) beanClass.getAnnotationsByType(BindConfig.class)[0];
    }
}
