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

import com.google.inject.MembersInjector;
import net.jmob.guice.conf.core.internal.ConfigurationException;
import net.jmob.guice.conf.core.internal.virtual.VirtualBeanFactory;

import java.lang.reflect.Field;

public class Injector<T> implements MembersInjector<T> {

    private final Field field;
    private final Object value;

    Injector(VirtualBeanFactory virtualBeanFactory) {
        this.field = virtualBeanFactory.getField();
        this.value = virtualBeanFactory.buildValue();
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T bean) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(e);
        }
    }
}
