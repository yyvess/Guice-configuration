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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.junit.Test;

public class BeanValidatorTest {

    @Test
    public void valid() {
        new BeanValidator().valid(new Bean().withValue("123456"), Bean.class);
    }

    @Test(expected = RuntimeException.class)
    public void validationFailNull() {
        new BeanValidator().valid(new Bean(), Bean.class);
    }

    @Test(expected = RuntimeException.class)
    public void validationFailInvalidLength() {
        new BeanValidator().valid(new Bean().withValue("123"), Bean.class);
    }

    public static class Bean {

        private String value;

        @NotNull
        @Length(min = 5)
        public String getValue() {
            return value;
        }

        public Bean withValue(String value) {
            this.value = value;
            return this;
        }
    }
}
