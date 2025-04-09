@file:Suppress("unused", "SameParameterValue")

package tech.mappie.api.updating

import tech.mappie.api.Mappie
import tech.mappie.api.ObjectMappingConstructor
import tech.mappie.api.generated

public abstract class ObjectUpdateMappie<UPDATER, T> : Mappie<T> {

    public open fun update(source: T, updater: UPDATER): T = generated()

    protected fun updating(builder: ObjectMappingConstructor<UPDATER, T>.() -> Unit = { }): T = generated()
}

