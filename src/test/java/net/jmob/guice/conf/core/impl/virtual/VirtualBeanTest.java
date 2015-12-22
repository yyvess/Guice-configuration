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

import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.*;

public class VirtualBeanTest {

    @Test
    public void test() {
        Map<String, Object> values = new HashMap<>();
        values.put("v", "short");
        values.put("value", "testing");
        values.put("int", 1);
        ServiceConfig service = newProxy(values);
        ServiceConfig service2 = newProxy(values);

        assertEquals(1, service.getInt());
        assertEquals("short", service.getV());
        assertEquals("testing", service.getValue());
        assertNull(service.getValueNull());
        assertEquals("{getV=short, getValue=testing, getInt=1}", service.toString());

        assertNotSame(service, service2);
        assertEquals(service, service);
        assertEquals(service, service2);
        assertEquals(service.hashCode(), service2.hashCode());
        assertFalse(service2.equals(null));
        assertFalse(service2.equals(new Object()));
    }

    @Test(expected = RuntimeException.class)
    public void testBadMethodName() {
        Map<String, Object> values = new HashMap<>();
        ServiceConfig service = newProxy(values);
        service.testBadMethodName();
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointer() {
        Map<String, Object> values = new HashMap<>();
        ServiceConfig service = newProxy(values);
        service.getIntNull();
    }

    private ServiceConfig newProxy(Map<String, Object> values) {
        ClassLoader cl = currentThread().getContextClassLoader();
        return (ServiceConfig) Proxy.newProxyInstance(cl, new Class[]{ServiceConfig.class}, new
                VirtualBean
                (values));
    }

    interface ServiceConfig {

        String getV();

        String getValue();

        String getValueNull();

        int getInt();

        int getIntNull();

        void testBadMethodName();
    }
}
