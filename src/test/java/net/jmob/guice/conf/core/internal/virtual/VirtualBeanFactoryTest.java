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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import net.jmob.guice.conf.core.internal.Typed;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class VirtualBeanFactoryTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private Config config;
    @Mock
    private ConfigObject configObject;
    @Mock
    private ConfigValue configValue;

    private BeanValidator beanValidator = new BeanValidator();

    private Integer field;

    private InterfaceTypedForTest interfaceField;

    private Optional<Integer> optional;

    @Test
    public void nominal() throws NoSuchFieldException {

        when(config.getAnyRef("test")).thenReturn(111);

        Field field = this.getClass().getDeclaredField("field");
        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withField(field)
                .withPath("test")
                .withType(Integer.class);

        Object value = virtualBeanFactory.buildValue();

        assertThat(virtualBeanFactory.getField(), is(field));
        assertThat(value, instanceOf(Integer.class));
        assertThat(value, is(111));
    }

    @Test
    public void optional() throws NoSuchFieldException {
        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withField(this.getClass().getDeclaredField("optional"))
                .withPath("optional")
                .withType(Optional.class);

        Object value = virtualBeanFactory.buildValue();

        assertThat(value, instanceOf(Optional.class));
        assertThat(value, is(Optional.empty()));
    }

    @Test
    public void interfaces() throws NoSuchFieldException {

        when(configValue.unwrapped()).thenReturn(999);
        when(config.getConfig("test")).thenReturn(config);
        when(config.root()).thenReturn(configObject);
        when(configObject.entrySet()).thenReturn(new HashSet<>(Collections.singleton(new
                AbstractMap.SimpleEntry<>("integer", configValue))));

        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withField(this.getClass().getDeclaredField("field"))
                .withPath("test")
                .withType(InterfaceForTest.class);

        Object value = virtualBeanFactory.buildValue();

        assertThat(value, instanceOf(InterfaceForTest.class));
        assertThat(((InterfaceForTest) value).getInteger(), is(999));
    }

    @Test
    public void typed() throws NoSuchFieldException {

        Map map0 = of("integer", 999);
        Map map1 = of("integer", 888);
        Map typedMap = of("key", map0);

        Map rootMap = of("interfaceForTest", map1, "typedMap", typedMap);

        when(config.getConfig("test")).thenReturn(ConfigFactory.parseMap(rootMap));

        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withField(this.getClass().getDeclaredField("interfaceField"))
                .withPath("test")
                .withType(InterfaceTypedForTest.class);

        Object value = virtualBeanFactory.buildValue();

        assertThat(value, instanceOf(InterfaceTypedForTest.class));
        assertThat(((InterfaceTypedForTest) value).getInterfaceForTest().getInteger(), is(888));
        assertThat(((InterfaceTypedForTest) value).getTypedMap().get("key"), instanceOf(InterfaceForTest.class));
        assertThat(((InterfaceTypedForTest) value).getTypedMap().get("key").getInteger(), is(999));
    }

    @Test(expected = RuntimeException.class)
    public void typeNotSupported() throws NoSuchFieldException {
        new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withPath("test")
                .withType(Float.class)
                .buildValue();
    }

    @Test
    public void missingPath() throws NoSuchFieldException {
        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator)
                .withConfig(config)
                .withPath("test/test")
                .withType(InterfaceForTest.class);

        Object value = virtualBeanFactory.buildValue();

        assertThat(value, instanceOf(InterfaceForTest.class));
        assertThat(((InterfaceForTest) value).getInteger(), is(nullValue()));
    }

    public interface InterfaceForTest {
        Integer getInteger();
    }

    public interface InterfaceTypedForTest {

        InterfaceForTest getInterfaceForTest();

        @Typed(InterfaceForTest.class)
        Map<String, InterfaceForTest> getTypedMap();
    }
}
