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
package net.jmob.guice.conf.core.impl.injector;

import net.jmob.guice.conf.core.impl.virtual.VirtualBeanFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InjectorTest {

    private String target = "Test";

    @Mock
    private VirtualBeanFactory virtualBeanFactory;


    @Test
    public void injectValue() throws IllegalAccessException, NoSuchFieldException {
        inject("Hello", true);
    }

    @Test
    public void injectNull() throws IllegalAccessException, NoSuchFieldException {
        inject(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void injectInvalidType() throws IllegalAccessException, NoSuchFieldException {
        inject(BigDecimal.ZERO, true);
    }

    @Test(expected = RuntimeException.class)
    public void notAccessibleField() throws IllegalAccessException, NoSuchFieldException {
        inject("Hello", false);
    }

    private void inject(Object value, boolean accessible) throws NoSuchFieldException,
            IllegalAccessException {
        final Field field = this.getClass().getDeclaredField("target");

        when(virtualBeanFactory.getField()).thenReturn(field);
        when(virtualBeanFactory.buildValue()).thenReturn(value);

        Injector injector = new Injector(virtualBeanFactory);
        if (!accessible) {
            field.setAccessible(accessible);
        }
        injector.injectMembers(this);

        assertThat(value, is(field.get(this)));
    }
}
