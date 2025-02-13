package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class IntToLongMapper : ObjectMappie<Int, Long>() {
    override fun map(from: Int): Long =
        from.toLong()
}

public class IntToStringMapper : ObjectMappie<Int, String>() {
    override fun map(from: Int): String =
        from.toString()
}
