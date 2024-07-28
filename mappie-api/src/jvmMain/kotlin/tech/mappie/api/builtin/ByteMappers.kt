package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigDecimal
import java.math.BigInteger

public class ByteToBigIntegerMapper : ObjectMappie<Byte, BigInteger>() {
    override fun map(from: Byte): BigInteger =
        BigInteger.valueOf(from.toLong())
}

public class ByteToBigDecimalMapper : ObjectMappie<Byte, BigDecimal>() {
    override fun map(from: Byte): BigDecimal =
        BigDecimal.valueOf(from.toLong())
}
