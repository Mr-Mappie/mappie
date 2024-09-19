import tech.mappie.api.ObjectMappie

data class NestedGeneratedInputClassObject(val value: InputClass)

data class InputClass(val value: Int)

data class NestedGeneratedOutputClassObject(val value: OutputClass)

data class OutputClass(val value: Int)

object NestedGeneratedInputClassToOutputClassMapper : ObjectMappie<NestedGeneratedInputClassObject, NestedGeneratedOutputClassObject>()