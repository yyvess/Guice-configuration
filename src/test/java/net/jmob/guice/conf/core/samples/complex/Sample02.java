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

package net.jmob.guice.conf.core.samples.complex;

import com.google.inject.AbstractModule;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.samples.complex.service.TypedEntry;
import org.junit.Before;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@BindConfig(value = "net/jmob/guice/conf/core/samples/sample_02", path = "root")
public class Sample02 {

    @InjectConfig(value = "port")
    private int port;

    @InjectConfig("complexType")
    private TypedEntry config;

    @Before
    public void init() {
        createInjector(new GuiceModule(this));
    }

    @Test
    public void test() {
        assertThat(port, is(12));
        assertThat(config, notNullValue());
        assertThat(config.getValue(), is("Hello World"));
        assertNotNull(config.getSubType());
        assertThat(config.getSubType().getIntValue(), is(of(9876)));
        assertNotNull(config.getAMap());
        assertThat(config.getAMap(), notNullValue());
        assertThat(config.getAMap().get("key1"), is("value1"));
        assertThat(config.getAMap().get("key2"), is("value2"));
        assertThat(config.getAList().get(0), is("value1"));
        assertThat(config.getAList().get(1), is("value2"));
        assertThat(config.getValue(), is("Hello World"));
        assertThat(config.getTypedMap().get("entry1").getValue(), is("Hello 1"));
        assertThat(config.getTypedMap().get("entry1").getIntValue(), is(of(1234)));
        assertThat(config.getTypedMap().get("entry2").getValue(), is("Hello 2"));
        assertThat(config.getTypedMap().get("entry2").getIntValue(), is(empty()));
        assertThat(config.toString(), is("{getAList=[value1, value2], getValue=Hello World, " +
                "getTypedMap={entry1={getValue=Hello 1, getIntValue=1234}, entry2={getValue=Hello 2}}, " +
                "getSubType={getIntValue=9876}, getAMap={key1=value1, key2=value2}}"));
        assertThat(config.hashCode(), is(-247958911));
    }

    public static class GuiceModule extends AbstractModule {

        private final Object test;

        public GuiceModule(Object test) {
            this.test = test;
        }

        @Override
        protected void configure() {
            install(ConfigurationModule.create());
            requestInjection(test);
        }
    }
}
