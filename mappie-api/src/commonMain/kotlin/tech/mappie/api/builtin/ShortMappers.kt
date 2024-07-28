package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class ShortToIntMapper : ObjectMappie<Short, Int>() {
    override fun map(from: Short): Int =
        from.toInt()
}

public class ShortToLongMapper : ObjectMappie<Short, Long>() {
    override fun map(from: Short): Long =
        from.toLong()
}
