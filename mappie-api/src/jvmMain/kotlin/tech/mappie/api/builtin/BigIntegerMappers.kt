package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.math.BigInteger

public class BigIntegerToStringMapper : ObjectMappie<BigInteger, String>() {
    override fun map(from: BigInteger): String =
        from.toString()
}
