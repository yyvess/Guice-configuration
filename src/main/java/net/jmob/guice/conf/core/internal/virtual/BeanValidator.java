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

import com.google.inject.Singleton;
import net.jmob.guice.conf.core.internal.ConfigurationException;
import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class BeanValidator {

    private final Logger log = getLogger(BeanValidator.class);
    private final ValidatorFactory factory;

    public BeanValidator() {
        ValidatorFactory f = null;
        try {
            f = Validation.buildDefaultValidatorFactory();
            log.debug("Validation factory builder found");
        } catch (ValidationException e) {
            log.debug("No validation factory found in classpath");
        } finally {
            factory = f;
        }
    }

    public <T> T valid(T bean, Class<?> beanInterface) {
        if (factory != null) {
            Set<ConstraintViolation<T>> constraintsViolation = factory.getValidator().validate(bean);
            if (!constraintsViolation.isEmpty()) {
                throw new ConfigurationException(
                        format("Constraint violation on %s : %s"
                                , beanInterface
                                , constraintsViolation.iterator().next().getMessage()));
            }
        }
        return bean;
    }
}
