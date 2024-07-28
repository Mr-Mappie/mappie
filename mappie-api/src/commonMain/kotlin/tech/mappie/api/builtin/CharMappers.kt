package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class CharToStringMapper : ObjectMappie<Char, String>() {
    override fun map(from: Char): String =
        from.toString()
}
