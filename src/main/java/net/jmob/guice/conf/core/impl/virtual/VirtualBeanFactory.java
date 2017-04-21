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

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import net.jmob.guice.conf.core.impl.Typed;

public class VirtualBeanFactory {

    private static final String GET_METHOD_PATTERN = VirtualBean.GET_PREFIX + ".+";
    private static final int GET_PREFIX_SIZE = VirtualBean.GET_PREFIX.length();
    private static final List<Class> SUPPORTED_TYPES
            = asList(int.class, Integer.class, double.class, Double.class, String.class, Map.class, List.class);

    private final BeanValidator beanValidator = new BeanValidator();

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
            throw new RuntimeException(format("Type not supported, must be a interface : %s", this.type));
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
                .map(e -> mapCandidateChild(beanInterface, e).orElse(e))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private Optional<Entry<String, Object>> mapCandidateChild(Class beanInterface, Entry<String, Object> e) {
        return stream(beanInterface.getMethods())
                .filter(f -> isCandidateMethod(e.getKey(), f))
                .filter(f -> e.getValue() instanceof Map)
                .map(f -> buildChildEntry(e.getKey(), f, (Map) e.getValue()))
                .findFirst();
    }

    private boolean isCandidateMethod(String key, Method method) {
        return (method.getReturnType().isInterface() && !SUPPORTED_TYPES.contains(method.getReturnType())
                || isAnnotationTypedPresent(method))
                && method.getName().matches(GET_METHOD_PATTERN)
                && toPropertyName(method.getName()).equals(key);
    }

    private Entry<String, Object> buildChildEntry(String key, Method m, Map<String, Object> values) {
        if (!isAnnotationTypedPresent(m)) {
            return new SimpleImmutableEntry<>(buildChildEntry(m, key, values));
        }
        return new SimpleImmutableEntry<>(key, values.entrySet().stream()
                .map(e -> buildChildEntry(m, e.getKey(), (Map<String, Object>) e.getValue()))
                .collect(toMap(Entry::getKey, Entry::getValue)));
    }

    private Entry<String, Map> buildChildEntry(Method m, String key, Map<String, Object> values) {
        return new SimpleImmutableEntry(key, newProxyInstance(getType(m), values));
    }

    private Class getType(Method m) {
        return isAnnotationTypedPresent(m) ? m.getAnnotationsByType(Typed.class)[0].value() : m.getReturnType();
    }

    private boolean isAnnotationTypedPresent(Method m) {
        return m.isAnnotationPresent(Typed.class);
    }

    private String toPropertyName(String methodName) {
        return methodName.substring(GET_PREFIX_SIZE).substring(0, 1).toLowerCase()
                .concat(methodName.substring(GET_PREFIX_SIZE + 1));
    }

    private Map<String, Object> getProperties() {
        return ofNullable(getRawProperties())
                .map(p -> p.stream().collect(toMap(Entry::getKey, e -> e.getValue().unwrapped())))
                .orElse(emptyMap());
    }

    private Set<Entry<String, ConfigValue>> getRawProperties() {
        return ofNullable(config.getConfig(path))
                .map(c -> c.root().entrySet())
                .orElse(emptySet());
    }

    public VirtualBeanFactory withPath(String path) {
        this.path = path;
        return this;
    }
}
