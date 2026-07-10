---
title: "Kotlin Toolchain Configuration"
summary: "Use Mappie via Kotlin Toolchain."
eleventyNavigation:
  key: Kotlin Toolchain Configuration
  parent: Getting Started
  order: 5
---

Mappie is a Kotlin compiler plugin which can be configured via the Kotlin Toolchain module system. We can apply Mappie by adding the following to the `module.yaml` file:

```yaml
settings:
  kotlin:
    compilerPlugins:
      - id: mappie
        dependency: tech.mappie:mappie-compiler-plugin:version
        options:
          output-dir: build/mappie
```

We must also add the `mappie-api` dependency:

```yaml
dependencies:
  - tech.mappie:mappie-api:version
```

The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.

## Configuration

Mappie can be configured via the compiler plugin options or per Mapper. The following configuration options are available:

```yaml
settings:
  kotlin:
    compilerPlugins:
      - id: mappie
        dependency: tech.mappie:mappie-compiler-plugin:version
        options:
          output-dir: build/mappie                                     # Required. 
          use-default-arguments: false                                 # Disable using default arguments in implicit mappings
          strict-enums: false                                          # Do not report an error if not all enum sources are mapped
          strict-platform-type-nullability: false                      # Allow unsafe assigning Java platform types to non-nullable targets
          strict-visibility: true                                      # Allow calling constructors not visible from the calling scope
          report-enabled: true                                         # Enable report generation
```

Local configuration options are applied as annotations on the class level of mappers, and will override the global configuration option on a per-mapper basis.

The following options exist with their corresponding default values:

| Kotlin Toolchain Option              | Annotation                                    | Default Value      |
|--------------------------------------|-----------------------------------------------|--------------------|
| `use-default-arguments`              | `@UseDefaultArguments`                        | `true`             |
| `strict-enums`                       | `@UseStrictEnums`                             | `true`             |
| `strict-platform-type-nullability`   | `@UseStrictPlatformTypeNullabilityValidation` | `true`             |
| `strict-visibility`                  | `@UseStrictVisibility`                        | `false`            |
| `report-enabled`                     |                                               | `false`            |
| `output-dir` (required)              |                                               | N/A                |
