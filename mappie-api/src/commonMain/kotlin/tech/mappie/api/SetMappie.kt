package tech.mappie.api

/**
 * Base mapper class for set mappers. Cannot be instantiated, but can be created by using the field [ObjectMappie.forSet].
 */
public sealed class SetMappie<out TO> : Mappie<Set<TO>>