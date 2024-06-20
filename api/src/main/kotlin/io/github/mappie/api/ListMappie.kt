package io.github.mappie.api

/**
 * Base mapper class for list mappers. Cannot be instantiated, but can be created by using the field [ObjectMappie.forList].
 */
sealed class ListMappie<FROM, TO> : Mappie<List<FROM>, List<TO>>()