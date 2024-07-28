package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigInteger

public class IntToBigIntegerMapper : ObjectMappie<Int, BigInteger>() {
    override fun map(from: Int): BigInteger =
        from.toBigInteger()
}
