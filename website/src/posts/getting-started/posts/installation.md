---
title: "Installation"
summary: "Applying Mappie to your project."
eleventyNavigation:
  key: Installation
  parent: Getting Started
  order: 2
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
    id("tech.mappie.plugin") version "x.y.z"
}</code></pre>
        </div>
        <div class="tab-pane" data-id="groovy">
            <pre><code class="language-groovy">plugins {
    id "tech.mappie.plugin" version "x.y.z"
}</code></pre>
        </div>
    </div>
    </div>
{% endraw %}

The `mappie-api` dependency must be added to the Gradle build file file for the programming interface
{% raw %}
<div class="nav-container">
    <ul class="nav">
        <li class="active"><a data-id="kotlin">build.gradle.kts</a></li>
        <li><a data-id="groovy">build.gradle</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" data-id="kotlin">
            <pre><code class="language-kotlin">dependencies {
    implementation("tech.mappie:mappie-api:x.y.z")
}</code></pre>
        </div>
        <div class="tab-pane" data-id="groovy">
            <pre><code class="language-groovy">dependencies {
    implementation "tech.mappie:mappie-api:x.y.z"
}</code></pre>
        </div>
    </div>
    </div>
{% endraw %}

The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.

## Compatibility

Mappie is dependent on compiler internals, which might be unstable. These dependencies are kept to a minimum,
but are unavoidable. Mappie is tested and compatible with Kotlin versions `1.9.24`, `2.0.0`, and higher.

Versions below `1.9.24` might work, but are untested. If you encounter an issue, please report this as a bug. 
Note that incompatible versions will possibly lead to compilation failure, and never to runtime risks.
