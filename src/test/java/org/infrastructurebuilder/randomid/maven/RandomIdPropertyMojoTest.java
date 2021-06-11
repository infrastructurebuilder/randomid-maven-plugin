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

import static org.infrastructurebuilder.randomid.maven.RandomConfig.DEFAULT_FORMAT;
import static org.infrastructurebuilder.randomid.maven.RandomConfig.DEFAULT_LENGTH;
import static org.infrastructurebuilder.randomid.maven.RandomConfig.DEFAULT_NAME;
import static org.infrastructurebuilder.randomid.maven.RandomConfig.DEFAULT_SPECIALS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomIdPropertyMojoTest {

  MavenProject         project;
  Path                 path;
  RandomIdPropertyMojo mojo;
  List<RandomConfig>   singleConfig, multiConfig;
  RandomConfig         single;

  @BeforeEach
  public void setUp() throws Exception {
    Model m = new Model();
    m.setModelEncoding("utf-8");
    m.setModelVersion("4.0.0");
    Properties p = new Properties();
    p.setProperty("X", "Y");
    m.setProperties(p);
    project = new MavenProject(m);
    mojo = new RandomIdPropertyMojo();
    mojo.project = project;
    single = new RandomConfig();
    single.setCount(-1);
    single.setName(DEFAULT_NAME);
    single.setFormat(DEFAULT_FORMAT);
    single.setLength(DEFAULT_LENGTH);
    single.setLower(1);
    single.setUpper(1);
    single.setNumbers(1);
    single.setSpecials(1);
    single.setSpecialSet(DEFAULT_SPECIALS);
    singleConfig = List.of(single);
  }

  @Test
  public void testLocalExecute() throws MojoExecutionException, IOException {
    mojo.randomConfigs = singleConfig;
    mojo.skip = false;
    mojo.failOnOverwrite = false;
    mojo.execute();
    assertNotNull(mojo.project.getProperties().getProperty(DEFAULT_NAME));
  }

  @Test
  public void testSkip() throws MojoExecutionException {
    mojo.randomConfigs = singleConfig;
    mojo.skip = true;
    mojo.execute();
    assertFalse(mojo.project.getProperties().containsKey(DEFAULT_NAME));
  }

  @Test
  public void testBroken1() {
    RandomConfig b1 = new RandomConfig();
    b1.setLength(1);
    b1.setNumbers(2);
    b1.setLower(2);
    mojo.randomConfigs = List.of(b1);
    mojo.skip = false;
    assertThrows(IllegalArgumentException.class, () -> mojo.execute());
  }

  @Test
  public void testBroken2() {
    RandomConfig b1 = new RandomConfig();
    assertThrows(IllegalArgumentException.class, () -> b1.setName(""));
    assertThrows(IllegalArgumentException.class, () -> b1.setLength(0));
  }

  @Test
  public void testBroken3() throws MojoExecutionException {
    RandomConfig b1 = new RandomConfig();
    b1.setNumbers(2);
    b1.setLower(2);
    b1.setCount(1);
    b1.setFormat("%5.5s");
    mojo.randomConfigs = List.of(b1);
    mojo.skip = false;
    mojo.project.getProperties().setProperty("rando", "x");
    mojo.failOnOverwrite = true;
    assertThrows(IllegalArgumentException.class, () -> mojo.execute());
  }
  @Test
  public void testBroken3b() throws MojoExecutionException {
    RandomConfig b1 = new RandomConfig();
    b1.setCount(2);
    b1.setFormat("%5.5s");
    mojo.randomConfigs = List.of(b1);
    mojo.skip = false;
    assertThrows(IllegalArgumentException.class, () -> mojo.execute());
  }
  @Test
  public void testBrokenish4() throws MojoExecutionException {
    RandomConfig b1 = new RandomConfig();
    b1.setNumbers(2);
    b1.setLower(2);
    b1.setCount(1);
    b1.setFormat("%5.5s");
    mojo.randomConfigs = List.of(b1);
    mojo.skip = false;
    mojo.project.getProperties().setProperty("rando", "x");
    mojo.failOnOverwrite = false;
    mojo.execute();
    assertFalse(mojo.project.getProperties().getProperty("rando").equals("x"));
  }
  @Test
  public void testMultiple() throws MojoExecutionException {
    RandomConfig b1 = new RandomConfig();
    b1.setNumbers(2);
    b1.setLower(2);
    b1.setCount(2);
    mojo.randomConfigs = List.of(b1);
    mojo.skip = false;
    mojo.execute();
    assertNotNull(mojo.project.getProperties().getProperty(String.format(DEFAULT_FORMAT, DEFAULT_NAME, 0)));
    assertNotNull(mojo.project.getProperties().getProperty(String.format(DEFAULT_FORMAT, DEFAULT_NAME, 1)));
    assertNull(mojo.project.getProperties().getProperty(String.format(DEFAULT_FORMAT, DEFAULT_NAME, 2)));
  }
}
