package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

public class LocalDateTimeToLocalTimeMapper : ObjectMappie<LocalDateTime, LocalTime>() {
    override fun map(from: LocalDateTime): LocalTime =
        from.toLocalTime()
}

public class LocalDateTimeToLocalDateMapper : ObjectMappie<LocalDateTime, LocalDate>() {
    override fun map(from: LocalDateTime): LocalDate =
        from.toLocalDate()
}
