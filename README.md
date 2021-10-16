# randomid-maven-plugin

A plugin that generates one or more random strings into the Maven project properties


## Usage

### Single
Add a single "random" property of length 16 with at least one uppercase, lowercase and number, no special characters
```
        <configuration>
          <randomConfigs>
            <randomConfig>
              <!-- Add a property "fred" that is a random uuid string -->
              <name>fred</name>
              <uuid>true</uuid>
            </randomConfig>
            <randomConfig>
              <!-- these are the default values -->
              <name>random</name>
              <length>16</length>
              <upper>1</upper>
              <lower>1</lower>
              <numbers>1</numbers>
              <specials>0</specials>
              <!-- You can more easily override the "special" characters with a CDATA... -->
              <specialSet><![CDATA["%!#()&[]|:;<>,./"]]></specialSet>
            </randomConfig>
          </randomConfigs>
        </configuration>
```

### Multiples
(Use Caution!)

Add "random0", "random1", "random2", and "random3", all with ostensibly different 16 character values
and one "ted" length 5
```
        <configuration>
          <randomConfigs>
            <randomConfig>
              <name>random</name>
              <count>4</count>
              <format>%s%d</count>
              <length>16</length>
            </randomConfig>
            <randomConfig>
              <name>ted</name>
              <length>5</length>
            </randomConfig>
          </randomConfigs>
        </configuration>
```
