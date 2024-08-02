package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.util.UUID

public class UUIDToStringMapper : ObjectMappie<UUID, String>() {
    override fun map(from: UUID): String =
        from.toString()
}
