package tech.mappie.api

public abstract class PredefinedMapperProvider {
    public abstract val common: List<String>
    public abstract val jvm: List<String>

    public companion object
}