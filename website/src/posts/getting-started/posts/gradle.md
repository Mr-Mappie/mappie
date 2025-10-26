---
title: "Gradle Configuration"
summary: "Use Mappie via Gradle."
eleventyNavigation:
  key: Gradle Configuration
  parent: Getting Started
  order: 3
---

Mappie is a Kotlin compiler plugin which comes with a Gradle plugin to automatically apply and configure the compiler 
plugin. We can apply Mappie by adding the following plugin to the Gradle build file
{% raw %}
<div class="nav-container">
    <ul class="nav">
        <li class="active"><a data-id="kotlin">build.gradle.kts</a></li>
        <li><a data-id="groovy">build.gradle</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" data-id="kotlin">
            <pre><code class="language-kotlin">plugins {
    id("tech.mappie.plugin") version "version"
}</code></pre>
        </div>
        <div class="tab-pane" data-id="groovy">
            <pre><code class="language-groovy">plugins {
    id "tech.mappie.plugin" version "version"
}</code></pre>
        </div>
    </div>
    </div>
{% endraw %}

When using mappie version below `1.0.0` or when you want to add the `mappie-api` dependency manually, 
the `mappie-api` dependency can be added as follows
{% raw %}
<div class="nav-container">
    <ul class="nav">
        <li class="active"><a data-id="kotlin">build.gradle.kts</a></li>
        <li><a data-id="groovy">build.gradle</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" data-id="kotlin">
            <pre><code class="language-kotlin">dependencies {
    implementation("tech.mappie:mappie-api:version")
}</code></pre>
        </div>
        <div class="tab-pane" data-id="groovy">
            <pre><code class="language-groovy">dependencies {
    implementation "tech.mappie:mappie-api:version"
}</code></pre>
        </div>
    </div>
    </div>
{% endraw %}

The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.

## Configuration

Mappie can be configured via Gradle or per Mapper. The following configuration options are available
```kotlin
mappie {
    useDefaultArguments = false // Disable using default arguments in implicit mappings
    strictness {
        enums = false // Do not report an error if not all enum sources are mapped
        platformTypeNullability = true // Enable strict nullability checks for cross-language mappings
        visibility = true // Allow calling constructors not visible from the calling scope
    }
    reporting {
        enabled = true // Enable report generation
    }
}
```

Local configuration options are applied as annotations on the class level of mappers, and will override the global
configuration option on a per-mapper basis.

The following options exist with their corresponding default values
| Gradle Option                        | Annotation                                    | Default Value      |
|--------------------------------------|-----------------------------------------------|--------------------|
| `useDefaultArguments`                | `@UseDefaultArguments`                        | `true`             |
| `strictness.enums`                   | `@UseStrictEnums`                             | `true`             |
| `strictness.platformTypeNullability` | `@UseStrictPlatformTypeNullabilityValidation` | `true`             |
| `strictness.visibility`              | `@UseStrictVisibility`                        | `false`            |
| `reporting.enabled`                  |                                               | `false`            |
| `reporting.directory`                |                                               | `$buildDir/mappie` |
