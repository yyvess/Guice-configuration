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
package net.jmob.guice.conf.core.samples.basic;

import com.google.inject.AbstractModule;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.google.inject.Guice.createInjector;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@BindConfig(value = "net/jmob/guice/conf/core/samples/sample_01")
public class ConfigFromConf {

    @InjectConfig
    private int port;

    @InjectConfig
    private Double aDouble;

    @InjectConfig
    private Integer aInteger;

    @InjectConfig
    private Boolean aBoolean;

    @InjectConfig
    private List<Integer> aIntList;

    @InjectConfig
    private Optional<Integer> aOptionalInteger;

    @InjectConfig
    private Optional<Integer> emptyInteger;

    @InjectConfig("complexEntries")
    private ServiceConfiguration config;

    @Before
    public void init() {
        createInjector(new GuiceModule(this));
    }

    @Test
    public void test() {
        assertThat(port, is(12));
        assertThat(aDouble, is(22.55));
        assertThat(aInteger, is(44));
        assertThat(aInteger, is(44));
        assertThat(aOptionalInteger, is(Optional.of(423)));
        assertThat(emptyInteger, is(Optional.empty()));
        assertThat(aBoolean, is(false));

        assertThat(aIntList, notNullValue());
        assertThat(aIntList.get(0), is(67));

        assertThat(config, notNullValue());
        assertThat(config.getValue(), is("Hello World"));
        assertThat(config.getBoolean(), is(true));
        assertThat(config.getAMap(), notNullValue());
        assertThat(config.getAMap().get("key1"), is("value1"));
        assertThat(config.getAMap().get("key2"), is("value2"));
        assertThat(config.getAList().get(0), is("value1"));
        assertThat(config.getAList().get(1), is("value2"));
    }

    public static class GuiceModule extends AbstractModule {

        private final Object test;

        public GuiceModule(Object test) {
            this.test = test;
        }

        @Override
        protected void configure() {
            install(new ConfigurationModule());
            requestInjection(test);
        }
    }
}
