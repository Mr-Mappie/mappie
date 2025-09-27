package tech.mappie.testing.gradle

class SettingsGradleBuilder {

    fun build(): String {
        return """
            pluginManagement {
                repositories {
                    mavenLocal {
                        content {
                            includeGroupByRegex("tech\\.mappie.*")
                        }
                    }
                    gradlePluginPortal {
                        content {
                            excludeGroupByRegex("tech\\.mappie*")    
                        }
                    }
                }
            }
            
            dependencyResolutionManagement {
                repositories {
                    mavenLocal {
                        content {
                            includeGroupByRegex("tech\\.mappie*")
                        }
                    }
                    mavenCentral {
                        content {
                            excludeGroupByRegex("tech\\.mappie*")    
                        }
                    }
                }
            }
            """.trimIndent()
    }
}