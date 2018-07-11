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

package net.jmob.guice.conf.core.internal.virtual;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.lang.Character.toUpperCase;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

final class VirtualBean implements InvocationHandler {

    static final String GET_PREFIX = "get";
    private static final String HASH_CODE_METHOD = "hashCode";
    private static final String EQUALS_METHOD = "equals";
    private static final String TO_STRING_METHOD = "toString";
    private final Map<String, Object> values = new HashMap<>();

    public VirtualBean(Map<String, Object> values) {
        for (Entry<String, Object> e : values == null ? this.values.entrySet() : values.entrySet()) {
            this.values.put(toGetMethodName(e.getKey()), e.getValue());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        int nbArgs = nbArgs(args);
        if (methodName.startsWith(GET_PREFIX) && nbArgs == 0) {
            return magicGet(methodName, Optional.class.isAssignableFrom(method.getReturnType()));
        } else if (methodName.equals(HASH_CODE_METHOD) && nbArgs == 0) {
            return hashCode();
        } else if (methodName.equals(TO_STRING_METHOD) && nbArgs == 0) {
            return toString();
        } else if (methodName.equals(EQUALS_METHOD) && nbArgs == 1) {
            return equals(args[0]);
        }
        throw new RuntimeException(format("Incorrect method name %s:%s", methodName, nbArgs));
    }

    private Object magicGet(String methodName, boolean optional) {
        return optional ? ofNullable(this.values.get(methodName)) : this.values.get(methodName);
    }

    private int nbArgs(Object[] args) {
        return args == null ? 0 : args.length;
    }

    private String toGetMethodName(String propertyName) {
        if (propertyName.length() <= 1) {
            return GET_PREFIX + propertyName.toUpperCase();
        }
        return GET_PREFIX + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    @Override
    public String toString() {
        return this.values.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (Proxy.isProxyClass(obj.getClass())) {
            return obj.equals(this);
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VirtualBean other = (VirtualBean) obj;
        return this.values.equals(other.values);
    }

    @Override
    public int hashCode() {
        final int prime = 47;
        return prime + this.values.hashCode();
    }
}
