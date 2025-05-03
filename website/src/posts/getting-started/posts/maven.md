---
title: "Maven Configuration"
summary: "Use Mappie via Maven."
eleventyNavigation:
  key: Maven Configuration
  parent: Getting Started
  order: 4
---

Mappie is a Kotlin compiler plugin which comes with a Maven plugin to automatically apply and configure the compiler
plugin. We can apply Mappie by adding the following to the `kotlin-maven-plugin` configuration in the pom

```xml
...
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>...</version>
    
    <configuration>
        <compilerPlugins>
            <compilerPlugin>mappie</compilerPlugin>
        </compilerPlugins>
        <pluginOptions>
            ...
        </pluginOptions>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>tech.mappie</groupId>
            <artifactId>mappie-maven-plugin</artifactId>
            <version>version</version>
        </dependency>
    </dependencies>
</plugin>
...
```

We must also add the `mappie-api` dependency. For example, for the JVM
```xml
<dependency>
    <groupId>tech.mappie</groupId>
    <artifactId>mappie-api-jvm</artifactId>
    <version>version</version>
</dependency>
```

The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.

## Configuration

Mappie can be configured via Maven or per Mapper. The following configuration options are available
```xml
<pluginOptions>
    <option>mappie:use-default-arguments=false</option> <!-- Disable using default arguments in implicit mappings -->
    <option>mappie:strict-visibility=true</option> <!-- Allow calling constructors not visible from the calling scope -->
    <option>mappie:strict-enums=false</option> <!-- Do not report an error if not all enum sources are mapped  -->
    <option>mappie:reporting-enabled=true</option> <!-- Enable report generation -->
</pluginOptions>
```

Local configuration options are applied as annotations on the class level of mappers, and will override the global
configuration option on a per-mapper basis.

The following options exist with their corresponding default values
| Maven Option                   | Annotation             | Default Value       |
|------------------------------- |------------------------|---------------------|
| `mappie:use-default-arguments` | `@UseDefaultArguments` | `true`              |
| `mappie:strict-visibility`     | `@UseStrictVisibility` | `false`             |
| `mappie:strict-enums`          | `@UseStrictEnums`      | `true`              |
| `mappie:report-enabled`        |                        | `false`             |
| `mappie:report-dir`            |                        | `$outputDir/mappie` |
