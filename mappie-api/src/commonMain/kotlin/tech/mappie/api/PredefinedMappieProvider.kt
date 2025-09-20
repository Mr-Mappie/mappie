package tech.mappie.api

public abstract class PredefinedMappieProvider {
    public abstract val common: List<String>
    public abstract val jvm: List<String>

    public companion object
}