package tech.mappie.api

/**
 * Base mapper class for list mappers. Cannot be instantiated, but can be created by using the field [ObjectMappie.forList].
 */
public sealed class ListMappie<out TO> : Mappie<List<TO>>