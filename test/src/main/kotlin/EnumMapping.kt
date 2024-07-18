import tech.mappie.api.EnumMappie

enum class InputEnum {
    A, B, C, D;
}

enum class OutputEnum {
    A, B, C, D, E;
}

object EnumMapper : EnumMappie<InputEnum, OutputEnum>()