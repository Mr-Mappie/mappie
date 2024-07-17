import tech.mappie.api.ObjectMappie

data class Input(val string: String)
data class Output(val string: String)

object Mapper : ObjectMappie<Input, Output>()
