package tech.mappie

import java.util.Properties

object MappieProperties {

    private val properties = javaClass.classLoader.getResourceAsStream("mappie.properties").use {
        Properties().apply { load(it) }
    }

    val version = properties["VERSION"] as String
}