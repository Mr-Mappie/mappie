package tech.mappie.api

public abstract class TwoWayObjectMappie<FROM, TO> : ObjectMappie<FROM, TO>() {

    public val inverse: ObjectMappie<TO, FROM> = generated()

    protected fun ObjectMappingConstructor<FROM, TO>.inverse(builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): Unit = generated()
}
