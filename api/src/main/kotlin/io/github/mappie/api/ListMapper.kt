package io.github.mappie.api

/**
 * Base mapper class for list mappers. Cannot be instantiated, but can be created by using the field [ObjectMapper.forList].
 */
sealed class ListMapper<FROM, TO> : Mapper<List<FROM>, List<TO>>()