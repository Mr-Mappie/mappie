package tech.mappie.api

import kotlin.reflect.KProperty

public class ObjectMappingConstructor<FROM, out TO> {

    /**
     * Alias for the target type [TO] to simply property references.
     *
     * For example, suppose we are constructing a mapper with target type `Person`
     * ```kotlin
     *  to::name fromProperty PersonDto::fullName
     * ```
     * is equivalent to `Person::name fromProperty PersonDto::fullName`.
     */
    public val to: TO
        get() = error("The to property should only be used in the context of `to::property fromX y`.")

    /**
     * Explicitly construct a mapping to [TO] from property source [source].
     *
     * For example
     * ```kotlin
     * Person::name fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    public infix fun <TO_TYPE, FROM_TYPE> KProperty<TO_TYPE>.fromProperty(source: KProperty<FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from a value source [value].
     *
     * For example
     * ```kotlin
     * Person::name fromValue "John Doe"
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe"`.
     */
    public infix fun <TO_TYPE> KProperty<TO_TYPE>.fromValue(value: TO_TYPE): Unit =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from expression source [function].
     *
     * For example
     * ```kotlin
     * Person::name fromExpression { personDto -> personDto.fullName + " (full)" }
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe (full)"`,
     * assuming `personDto.fullName == "John Doe"`.
     */
    public infix fun <FROM_TYPE, TO_TYPE> KProperty<TO_TYPE>.fromExpression(function: (FROM) -> FROM_TYPE): Unit =
        generated()

    /**
     * Reference a constructor parameter or target property in lieu of a property reference, if it not exists as a property.
     *
     * For example
     * ```kotlin
     * to("name") fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `name` to `PersonDto.fullName`.
     */
    public fun to(name: String): KProperty<*> =
        generated()
}

public class MultipleObjectMappingConstructor<out TO> {

    /**
     * Alias for the target type [TO] to simply property references.
     *
     * For example, suppose we are constructing a mapper with target type `Person`
     * ```kotlin
     *  to::name fromProperty PersonDto::fullName
     * ```
     * is equivalent to `Person::name fromProperty PersonDto::fullName`.
     */
    public val to: TO
        get() = error("The to property should only be used in the context of `to::property fromX y`.")

    /**
     * Explicitly construct a mapping to [TO] from property source [source].
     *
     * For example
     * ```kotlin
     * Person::name fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    public infix fun <TO_TYPE, FROM_TYPE> KProperty<TO_TYPE>.fromProperty(source: KProperty<FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from a value source [value].
     *
     * For example
     * ```kotlin
     * Person::name fromValue "John Doe"
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe"`.
     */
    public infix fun <TO_TYPE> KProperty<TO_TYPE>.fromValue(value: TO_TYPE): Unit =
        generated()

    /**
     * Reference a constructor parameter or target property in lieu of a property reference, if it not exists as a property.
     *
     * For example
     * ```kotlin
     * to("name") fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `name` to `PersonDto.fullName`.
     */
    public fun to(name: String): KProperty<*> =
        generated()
}