package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class FloatToDoubleMapper : ObjectMappie<Float, Double>() {
    override fun map(from: Float): Double =
        from.toDouble()
}

public class FloatToStringMapper : ObjectMappie<Float, String>() {
    override fun map(from: Float): String =
        from.toString()
}
