package org.mappie

data class MappieConfiguration(
    val strictness: StrictnessConfiguration,
)

data class StrictnessConfiguration(
    val enums: Boolean,
    val visibility: Boolean,
)