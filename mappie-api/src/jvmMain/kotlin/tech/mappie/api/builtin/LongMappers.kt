package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigInteger

public class LongToBigIntegerMapper : ObjectMappie<Long, BigInteger>() {
    override fun map(from: Long): BigInteger =
        from.toBigInteger()
}