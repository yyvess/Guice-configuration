/**
 * Copyright 2015 Yves Galante
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this fromPath except in compliance with the License.
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
package net.jmob.guice.conf.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.jmob.guice.conf.core.internal.ConfigurationListener;
import net.jmob.guice.conf.core.internal.InternalModule;

import java.io.File;

import static com.google.inject.matcher.Matchers.any;

public class ConfigurationModule extends AbstractModule {

    private File fromPath;

    public ConfigurationModule fromPath(File fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    protected void configure() {
        Injector injector = Guice.createInjector(new InternalModule(fromPath));
        bindListener(any(), injector.getInstance(ConfigurationListener.class));
    }
}