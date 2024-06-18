package io.github.mappie.api

/**
 * The result of an explicit mapping definition which can be transformed.
 */
class TransformableValue<FROM, TO> {
    /**
     *
     */
    infix fun transform(function: (FROM) -> TO): Unit = generated()

    infix fun <M : Mapper<FROM, TO>> via(clazz: M): M = generated()
}
