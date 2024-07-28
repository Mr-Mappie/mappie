package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigInteger

public class ShortToBigIntegerMapper : ObjectMappie<Short, BigInteger>() {
    override fun map(from: Short): BigInteger =
        BigInteger.valueOf(from.toLong())
}
