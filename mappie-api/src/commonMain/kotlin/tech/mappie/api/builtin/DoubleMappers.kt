package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class DoubleToStringMapper : ObjectMappie<Double, String>() {
    override fun map(from: Double): String =
        from.toString()
}
