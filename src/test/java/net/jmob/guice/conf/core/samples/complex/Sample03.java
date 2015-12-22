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
import com.google.inject.Guice;
import com.google.inject.Inject;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.samples.complex.service.Service;
import net.jmob.guice.conf.core.samples.complex.service.ServiceConf;
import net.jmob.guice.conf.core.samples.complex.service.ServiceJson;
import net.jmob.guice.conf.core.samples.complex.service.TypedEntry;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Named;

import static com.google.inject.name.Names.named;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Sample03 {

    @Inject
    @Named("Conf")
    private Service serviceConf;

    @Inject
    @Named("Json")
    private Service serviceJson;

    @Before
    public void init() {
        Guice.createInjector(new SampleModule(this));
    }

    @Test
    public void testConf() {
        assertEquals(12, serviceConf.getPort());
        check(serviceConf);
    }

    @Test
    public void testJson() {
        assertEquals(13, serviceJson.getPort());
        check(serviceJson);
    }

    private void check(Service service) {
        final TypedEntry config = service.getConfig();
        assertNotNull(config);
        assertEquals("Hello World", config.getValue());
        assertNotNull(config.getAMap());
        assertEquals("value1", config.getAMap().get("key1"));
        assertEquals("value2", config.getAMap().get("key2"));
        assertEquals("value1", config.getAList().get(0));
        assertEquals("value2", config.getAList().get(1));
        assertEquals("{getAList=[value1, value2], getValue=Hello World, getTypedMap={entry1={getValue=Hello 1, getIntValue=1234}, entry2={getValue=Hello 2}}, getAMap={key1=value1, key2=value2}}"
                , config.toString());
        assertEquals(-1633332542, config.hashCode());
    }

    public static class SampleModule extends AbstractModule {

        private final Sample03 sample03;

        public SampleModule(Sample03 sample03) {
            this.sample03 = sample03;
        }

        @Override
        protected void configure() {
            install(ConfigurationModule.create());
            requestInjection(sample03);
            bind(Service.class).annotatedWith(named("Conf")).to(ServiceConf.class);
            bind(Service.class).annotatedWith(named("Json")).to(ServiceJson.class);
        }
    }
}
