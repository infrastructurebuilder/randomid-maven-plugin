/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.randomid.maven;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;

public class RandomGeneratorConfig implements Supplier<Properties> {
  private final List<RandomConfig> randomConfigs;
  private final boolean            failOnOverwrite;
  private final Properties         localProperties;
  private final Log                log;

  public RandomGeneratorConfig(boolean failOnOverwrite, Log log, List<RandomConfig> randomConfigs,
      Properties localProperties) {
    this.randomConfigs = requireNonNull(randomConfigs, "No configurations supplied");
    this.failOnOverwrite = failOnOverwrite;
    this.localProperties = requireNonNull(localProperties, "No local properties supplied");
    this.log = requireNonNull(log, "No logger provided");
  }

  @Override
  public Properties get() {
    Properties p = new Properties();
    for (RandomConfig rc : randomConfigs) {
      Properties np = rc.get();
      np.stringPropertyNames().stream().filter(n -> p.containsKey(n) || localProperties.containsKey(n)).findAny()
          .ifPresent(k -> {
            if (failOnOverwrite)
              throw new IllegalArgumentException("Overwriting is disallowed " + k);
            else
              log.warn("Overwriting existing value of " + k);
          });
      p.putAll(np);
    }
    return p;
  }
}
