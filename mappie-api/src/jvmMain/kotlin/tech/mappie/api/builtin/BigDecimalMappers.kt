package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal

public class BigDecimalToStringMapper : ObjectMappie<BigDecimal, String>() {
    override fun map(from: BigDecimal): String =
        from.toString()
}
