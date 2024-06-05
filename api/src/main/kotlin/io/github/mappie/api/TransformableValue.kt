package io.github.mappie.api

class TransformableValue<FROM, TO> {
    infix fun transform(function: (FROM) -> TO): Unit = generated()

    infix fun via(clazz: Mapper<FROM, TO>): Unit = generated()
}