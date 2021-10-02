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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.internal.injector.Injector;
import net.jmob.guice.conf.core.internal.injector.InjectorBuilder;
import net.jmob.guice.conf.core.internal.virtual.BeanValidator;
import net.jmob.guice.conf.core.internal.virtual.VirtualBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationListenerTest {

    @Mock
    private TypeLiteral typeLiteral;

    @Mock
    private TypeEncounter typeEncounter;

    @Mock
    private ConfigFactory configFactory;

    @Mock
    private Config config;

    @Mock
    private BeanValidator beanValidator;

    private InjectorBuilder injectorBuilder;

    @BeforeEach
    void initializeMock() {
        VirtualBeanFactory virtualBeanFactory = new VirtualBeanFactory(beanValidator);
        injectorBuilder = new InjectorBuilder(configFactory, virtualBeanFactory);
        lenient().when(configFactory.parseResources(anyString(), any(ConfigParseOptions.class)))
                .thenReturn(config);
    }

    @Test
    void no_class_match() {
        when(typeLiteral.getRawType())
                .thenReturn(String.class);
        new ConfigurationListener(injectorBuilder)
                .hear(typeLiteral, typeEncounter);
        verify(typeEncounter, never()).register(any(Injector.class));
    }

    @Test
    void one_class_match() {
        when(typeLiteral.getRawType())
                .thenReturn(ConfiguredClass.class);
        new ConfigurationListener(injectorBuilder)
                .hear(typeLiteral, typeEncounter);
        verify(typeEncounter, times(1)).register(any(Injector.class));
    }

    @Test
    void super_class_match() {
        when(typeLiteral.getRawType())
                .thenReturn(Extends.class);
        new ConfigurationListener(injectorBuilder)
                .hear(typeLiteral, typeEncounter);
        verify(typeEncounter, times(1)).register(any(Injector.class));
    }

    @BindConfig(value = "rootPath")
    static class ConfiguredClass {
        @InjectConfig
        private Optional<String> p1;
    }

    private static class Extends extends ConfiguredClass {

    }
}
