package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal

public class DoubleToBigDecimalMapper : ObjectMappie<Double, BigDecimal>() {
    override fun map(from: Double): BigDecimal =
        from.toBigDecimal()
}