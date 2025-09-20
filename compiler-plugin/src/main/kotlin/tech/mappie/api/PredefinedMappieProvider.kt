package tech.mappie.api

// NOTE: copy of :mappie-api:tech.mappie.apiPredefinedMappieProvider
public abstract class PredefinedMappieProvider {
    public abstract val common: List<String>
    public abstract val jvm: List<String>

    public companion object
}