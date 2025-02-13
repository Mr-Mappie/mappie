package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal

public class FloatToBigDecimalMapper : ObjectMappie<Float, BigDecimal>() {
    override fun map(from: Float): BigDecimal =
        from.toBigDecimal()
}