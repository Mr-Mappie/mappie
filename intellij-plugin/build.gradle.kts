plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3.1")

        bundledPlugins(
            "org.jetbrains.kotlin",
            "com.intellij.gradle",
        )
        instrumentationTools()
    }
}
