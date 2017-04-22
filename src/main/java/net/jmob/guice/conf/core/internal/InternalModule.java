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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.io.File;

public class InternalModule extends AbstractModule {

    private final File from;

    public InternalModule(File fromPath) {
        this.from = fromPath;
    }

    @Override
    protected void configure() {
    }

    @Provides
    public ConfigFactory configFactory() {
        return new ConfigFactory(from);
    }
}