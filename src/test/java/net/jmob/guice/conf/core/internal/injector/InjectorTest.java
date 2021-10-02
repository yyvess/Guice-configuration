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

import net.jmob.guice.conf.core.internal.virtual.VirtualBeanFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InjectorTest {

    private String target = "Test";

    @Mock
    private VirtualBeanFactory virtualBeanFactory;


    @Test
    void injectValue() throws IllegalAccessException, NoSuchFieldException {
        inject("Hello", true);
    }

    @Test
    void injectNull() throws IllegalAccessException, NoSuchFieldException {
        inject(null, true);
    }

    @Test
    void injectInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> inject(BigDecimal.ZERO, true));
    }

    @Test
    void notAccessibleField() {
        assertThrows(RuntimeException.class, () -> inject("Hello", false));
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
