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

import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

import java.io.File;

import static com.typesafe.config.ConfigFactory.parseFileAnySyntax;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static java.io.File.separator;
import static java.util.Objects.isNull;

@Singleton
public final class ConfigFactory {

    private final File from;

     ConfigFactory(File from) {
        this.from = from;
    }

    public Config parseResources(String resourceBasename, ConfigParseOptions options) {
        if (isNull(this.from)) {
            return parseResourcesAnySyntax(resourceBasename, options);
        }
        return parseFileAnySyntax(new File(from.getAbsolutePath() + separator + resourceBasename), options);

    }
}
