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

package net.jmob.guice.conf.core.impl.virtual;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;
import net.jmob.guice.conf.core.impl.Typed;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

public class VirtualBeanFactory {

    private static final String GET_METHOD_PATTERN = VirtualBean.GET_PREFIX + ".+";
    private static final List<Class> SUPPORTED_TYPES = asList(int.class, Integer.class,
            double.class, Double.class, String.class, Map.class, List.class);

    private final BeanValidator beanValidator = new BeanValidator();

    private final Logger log = getLogger(this.getClass());

    private Class<?> type;
    private boolean optionalType;
    private Config config;
    private String path;
    private Field field;


    public VirtualBeanFactory withConfig(Config config) {
        this.config = config;
        return this;
    }

    public VirtualBeanFactory withType(Class<?> type) {
        this.type = type;
        this.optionalType = Optional.class.isAssignableFrom(type);
        return this;
    }

    public VirtualBeanFactory withField(Field field) {
        this.field = field;
        return this;
    }

    public Field getField() {
        return field;
    }

    public Object buildValue() {
        return optionalType ? ofNullable(getValue()) : getValue();
    }

    private Object getValue() {
        if (optionalType && !config.hasPath(path)) {
            return null;
        } else if (optionalType || SUPPORTED_TYPES.contains(type)) {
            return config.getAnyRef(path);
        } else if (!type.isInterface()) {
            throw new RuntimeException(format("Type is not supported, must be a interface : %s", this.type));
        }
        return newProxyInstance(type, mapProperties(type, getProperties()));
    }

    @SuppressWarnings("unchecked")
    private <T> T newProxyInstance(Class<T> beanInterface, Map<String, Object> values) {
        ClassLoader cl = currentThread().getContextClassLoader();
        Class<?>[] interfaces = {beanInterface};
        T bean = (T) Proxy.newProxyInstance(cl, interfaces, new VirtualBean(values));
        return beanValidator.valid(bean, beanInterface);
    }

    private Map<String, Object> mapProperties(Class beanInterface, Map<String, Object> values) {
        return values.entrySet().stream()
                .map(e -> mapChild(beanInterface, e).orElse(e))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private Optional<Entry<String, Object>> mapChild(Class beanInterface, Entry<String, Object> e) {
        return asList(beanInterface.getMethods()).stream()
                .filter(f -> isCandidateMethod(e.getKey(), f))
                .filter(f -> e.getValue() instanceof Map)
                .map(f -> buildChildEntries(e.getKey(), f, (Map) e.getValue()))
                .findFirst();
    }

    private boolean isCandidateMethod(String key, Method method) {
        return method.isAnnotationPresent(Typed.class)
                && method.getName().matches(GET_METHOD_PATTERN)
                && toPropertyName(method.getName()).equals(key);
    }

    private Entry<String, Object> buildChildEntries(String key, Method f, Map<String, Object> values) {
        return new SimpleImmutableEntry<>(key, values.entrySet().stream()
                .map(e -> buildChildEntry(f, e))
                .collect(toMap(Entry::getKey, Entry::getValue)));
    }

    private Entry<String, Map> buildChildEntry(Method f, Entry<String, Object> e) {
        return new SimpleImmutableEntry(e.getKey(), newProxyInstance(f.getAnnotationsByType(Typed.class)[0].value(), (Map) e.getValue()));
    }

    private String toPropertyName(String methodName) {
        return methodName.substring(3).substring(0, 1).toLowerCase().concat(methodName.substring(4));
    }

    private Map<String, Object> getProperties() {
        return ofNullable(getRawProperties())
                .map(p -> p.stream().collect(toMap(Entry::getKey, e -> e.getValue().unwrapped())))
                .orElse(emptyMap());
    }

    private Set<Entry<String, ConfigValue>> getRawProperties() {
        try {
            return config.getConfig(path).root().entrySet();
        } catch (ConfigException.Missing e) {
            this.log.debug(format("No configuration found on path %s", path));
        }
        return emptySet();
    }

    public VirtualBeanFactory withPath(String path) {
        this.path = path;
        return this;
    }
}
