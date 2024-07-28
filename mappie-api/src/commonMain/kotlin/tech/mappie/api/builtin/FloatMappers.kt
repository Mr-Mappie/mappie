package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class FloatToDoubleMapper : ObjectMappie<Float, Double>() {
    override fun map(from: Float): Double =
        from.toDouble()
}