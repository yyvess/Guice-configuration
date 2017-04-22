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

package net.jmob.guice.conf.core.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.internal.injector.InjectorBuilder;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ConfigurationListener implements TypeListener {

    private final InjectorBuilder injectorBuilder;

    @Inject
    public ConfigurationListener(InjectorBuilder injectorBuilder) {
        this.injectorBuilder = injectorBuilder;
    }

    @Override
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        getClasses(typeLiteral.getRawType(), new ArrayList<>()).stream()
                .filter(c -> c.isAnnotationPresent(BindConfig.class))
                .forEach(c -> injectorBuilder.build(c)
                        .forEach(typeEncounter::register));
    }

    private List<Class> getClasses(Class c, ArrayList<Class> classes) {
        if (c == null) {
            return classes;
        }
        classes.add(c);
        return getClasses(c.getSuperclass(), classes);
    }
}
