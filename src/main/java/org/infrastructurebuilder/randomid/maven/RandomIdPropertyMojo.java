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

import static java.lang.String.format;

import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "random", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true)
public class RandomIdPropertyMojo extends AbstractMojo {

  @Parameter(required = true, readonly = true, defaultValue = "${project}")
  public MavenProject       project;

  @Parameter(required = false, defaultValue = "false")
  public boolean            skip;

  @Parameter(required = false)
  public List<RandomConfig> randomConfigs;

  @Parameter(required = false)
  public boolean            failOnOverwrite;

  @Override
  public void execute() throws MojoExecutionException {
    Properties p;
    if (!skip) {
      p = new RandomGeneratorConfig(failOnOverwrite, getLog(), randomConfigs, project.getProperties()).get();
      String v;
      getLog().info(format("Acquired %d properties", p.size()));
      for (String n : p.stringPropertyNames()) {
        v = p.getProperty(n);
        getLog().debug(format("Setting %s=%s", n, v));
        project.getProperties().setProperty(n, p.getProperty(n));
      }
    } else {
      getLog().info(format("Skipping random property generation"));
    }
  }

}
