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
import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class RandomConfig implements Supplier<Properties> {
  public static final String DEFAULT_SPECIALS = "%!#()&[]|:;<>,./";
  public final static String UPPER            = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public final static String LOWER            = UPPER.toLowerCase();
  public final static String DIGITS           = "0123456789";
  public static final String DEFAULT_NAME     = "random";
  public static final String DEFAULT_FORMAT   = "%s%d";
  public static final int    DEFAULT_LENGTH   = 16;

  private String             name             = DEFAULT_NAME;
  private int                count            = 0;
  private int                length           = DEFAULT_LENGTH;
  private int                lower            = 0;
  private int                upper            = 0;
  private int                specials         = 0;
  private int                numbers          = 0;
  private String             specialSet       = DEFAULT_SPECIALS;
  private String             format           = DEFAULT_FORMAT;
  private boolean            uuid             = false;

  public void setUuid(boolean uuid) {
    this.uuid = uuid;
  }

  public void setName(String name) {
    this.name = requireNonNull(name, "Name must not be null");
    if (this.name.length() == 0)
      throw new IllegalArgumentException("Name length must be > 0");
  }

  public void setCount(int count) {
    this.count = (count > 0) ? count : 0;
  }

  public void setFormat(String format) {
    this.format = requireNonNull(format, "Format must not be null");
  }

  public void setLength(int len) {
    if (len < 1)
      throw new IllegalArgumentException("Length must be > 0");
    this.length = len;
  }

  public void setLower(int lower) {
    this.lower = lower;
  }

  public void setUpper(int upper) {
    this.upper = upper;
  }

  public void setSpecials(int specials) {
    this.specials = specials;
  }

  public void setNumbers(int numbers) {
    this.numbers = numbers;
  }

  public void setSpecialSet(String setOfSpecials) {
    this.specialSet = setOfSpecials;
  }

  @Override
  public Properties get() {
    int req = numbers + lower + upper + specials;
    if (length < req)
      throw new IllegalArgumentException(format("Length %d is less than required length %d", length, req));
    String     s = UPPER + LOWER + DIGITS + specialSet;
    Random     r = new Random(Instant.now().toEpochMilli());
    Properties p = new Properties();
    if (count == 0) {
      // single
      p.setProperty(name, getRandomString(s, r));
    } else {
      for (int i = 0; i < count; ++i) {
        String n = format(format, name, i);
        if (p.containsKey(n))
          throw new IllegalArgumentException("Format string causes collision (" + n + ")");
        p.setProperty(n, getRandomString(s, r));
      }
    }
    return p;
  }

  private String getRandomString(String s, Random r) {
    StringBuilder sb     = new StringBuilder();
    int           len    = s.length();
    int           cUpper = upper, cLower = lower, cSpecial = specials, cNumber = numbers;
    if (uuid)
      return UUID.randomUUID().toString();
    while (sb.length() < length) {
      char c = s.charAt(r.nextInt(len));
      if (Character.isUpperCase(c) && cUpper > 0) {
        cUpper--;
        sb.append(c);
      } else if (Character.isLowerCase(c) && cLower > 0) {
        cLower--;
        sb.append(c);
      } else if (Character.isDigit(c) && cNumber > 0) {
        cNumber--;
        sb.append(c);
      } else if (specialSet.indexOf(c) != -1 && cSpecial > 0) {
        cSpecial--;
        sb.append(c);
      } else if ((cUpper + cLower + cNumber + cSpecial) == 0)
        sb.append(c);
    }
    return sb.toString();
  }

}
