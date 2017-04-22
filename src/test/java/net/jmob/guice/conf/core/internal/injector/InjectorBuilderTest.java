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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigParseOptions;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.internal.ConfigFactory;
import net.jmob.guice.conf.core.internal.virtual.BeanValidator;
import net.jmob.guice.conf.core.internal.virtual.VirtualBeanFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@BindConfig(value = "rootPath")
public class InjectorBuilderTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private ConfigFactory configFactory;

    @Mock
    private Config config;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<String> arg;

    private VirtualBeanFactory virtualBeanFactory;

    private InjectorBuilder injectorBuilder;

    @Before
    public void initializeMock() {
        when(configFactory.parseResources(arg.capture(), any(ConfigParseOptions.class)))
                .thenReturn(config);
        virtualBeanFactory = new VirtualBeanFactory(beanValidator);
        injectorBuilder = new InjectorBuilder(configFactory, virtualBeanFactory);
    }

    @Test
    public void bean_without_property() {
        Stream<Injector> build = injectorBuilder.build(NoProperty.class);

        verify(config, never()).getConfig(anyString());
        assertThat(build.count(), is(0L));
    }

    @Test
    public void bean_config_with_properties() {
        Stream<Injector> build = injectorBuilder.build(WithProperties.class);

        verify(config, never()).getConfig(anyString());
        verify(config, never()).resolve();
        assertThat(arg.getValue(), is("rootPath"));
        assertThat(build.count(), is(2L));
    }

    @Test
    public void bean_config_with_resolve() {
        when(config.resolve())
                .thenReturn(config);

        Stream<Injector> build = injectorBuilder.build(WithResolve.class);

        verify(config, never()).getConfig(anyString());
        verify(config, times(1)).resolve();
        assertThat(build.count(), is(1L));
    }

    @Test
    public void bean_config_with_path() {
        when(config.getConfig("path"))
                .thenReturn(config);

        Stream<Injector> build = injectorBuilder.build(WithPath.class);

        verify(config).getConfig("path");
        verify(config, never()).resolve();
        assertThat(build.count(), is(1L));
    }

    @Test(expected = ConfigException.class)
    public void bind_config_with_invalid_path_must_throw_a_exception() {
        when(config.getConfig("path"))
                .thenThrow(ConfigException.BadPath.class);

        injectorBuilder.build(WithPath.class);
    }

    @BindConfig(value = "rootPath")
    public static class NoProperty {

    }

    @BindConfig(value = "rootPath")
    public static class WithProperties {
        @InjectConfig
        private Optional<String> p1;
        @InjectConfig
        private Optional<String> p2;
    }

    @BindConfig(value = "rootPath", resolve = true)
    public static class WithResolve {
        @InjectConfig
        private Optional<String> p1;
    }

    @BindConfig(value = "rootPath", path = "path")
    public static class WithPath {
        @InjectConfig
        private Optional<String> p1;
    }
}
