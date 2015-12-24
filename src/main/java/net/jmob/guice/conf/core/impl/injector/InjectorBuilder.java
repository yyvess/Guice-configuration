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

package net.jmob.guice.conf.core.impl.injector;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.impl.virtual.VirtualBeanFactory;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static com.typesafe.config.ConfigParseOptions.defaults;
import static java.util.Arrays.asList;

public class InjectorBuilder {

    private final VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory();

    private final Config config;
    private final Class beanClass;

    public InjectorBuilder(Class beanClass) {
        this.beanClass = beanClass;
        if (getAnnotationPath().isEmpty()) {
            this.config = parseResourcesAnySyntax(getAnnotationValue(), getOptions());
        } else {
            this.config = parseResourcesAnySyntax(getAnnotationValue(), getOptions()).getConfig(getAnnotationPath());
        }
    }

    public Stream<Injector> build() {
        return asList(this.beanClass.getDeclaredFields()).stream()
                .filter(f -> f.isAnnotationPresent(InjectConfig.class))
                .map(f -> virtualBeanFactory
                        .withConfig(this.config)
                        .withPath(getAnnotationPath(f))
                        .withType(f.getType())
                        .withField(f))
                .map(Injector::new);
    }

    private String getAnnotationPath(Field f) {
        final String annotationValue = f.getAnnotationsByType(InjectConfig.class)[0].value();
        return isNullOrEmpty(annotationValue) ? f.getName() : annotationValue;
    }

    private String getAnnotationPath() {
        return getAnnotationConfiguration().path();
    }

    private String getAnnotationValue() {
        return getAnnotationConfiguration().value();
    }

    private ConfigParseOptions getOptions() {
        return defaults().setSyntax(ConfigSyntax.valueOf(getAnnotationConfiguration().syntax().name()));
    }

    private BindConfig getAnnotationConfiguration() {
        return (BindConfig) beanClass.getAnnotationsByType(BindConfig.class)[0];
    }
}
