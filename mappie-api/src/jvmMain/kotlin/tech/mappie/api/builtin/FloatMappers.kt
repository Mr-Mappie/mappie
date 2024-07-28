package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal
import java.math.BigInteger

public class FloatToBigDecimalMapper : ObjectMappie<Float, BigDecimal>() {
    override fun map(from: Float): BigDecimal =
        from.toBigDecimal()
}