package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal
import java.math.BigInteger

public class IntToBigIntegerMapper : ObjectMappie<Int, BigInteger>() {
    override fun map(from: Int): BigInteger =
        from.toBigInteger()
}

public class IntToBigDecimalMapper : ObjectMappie<Int, BigDecimal>() {
    override fun map(from: Int): BigDecimal =
        from.toBigDecimal()
}

