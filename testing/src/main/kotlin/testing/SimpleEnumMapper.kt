package testing

import tech.mappie.api.EnumMappie

enum class Status {
    ON,
    OFF;
}

enum class StatusDto {
    ON,
    OFF;
}

object StatusMapper : EnumMappie<Status, StatusDto>()