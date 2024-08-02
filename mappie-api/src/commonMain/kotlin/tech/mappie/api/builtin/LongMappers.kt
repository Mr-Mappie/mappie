package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class LongToStringMapper : ObjectMappie<Long, String>() {
    override fun map(from: Long): String =
        from.toString()
}
