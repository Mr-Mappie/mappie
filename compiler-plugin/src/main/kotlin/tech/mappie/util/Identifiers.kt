package tech.mappie.util

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val IDENTIFIER_MAPPING = Name.identifier("mapping")

val IDENTIFIER_MAP = Name.identifier("map")

val IDENTIFIER_MAP_NULLABLE = Name.identifier("mapNullable")

val IDENTIFIER_MAP_NULLABLE_LIST = Name.identifier("mapNullableList")

val IDENTIFIER_MAP_LIST = Name.identifier("mapList")

val IDENTIFIER_MAP_NULLABLE_SET = Name.identifier("mapNullableSet")

val IDENTIFIER_MAP_SET = Name.identifier("mapSet")

val IDENTIFIER_FROM_ENUM_ENTRY = Name.identifier("fromEnumEntry")

val IDENTIFIER_THROWN_BY_ENUM_ENTRY = Name.identifier("thrownByEnumEntry")

val IDENTIFIER_FROM_PROPERTY = Name.identifier("fromProperty")

val IDENTIFIER_FROM_PROPERTY_NOT_NULL = Name.identifier("fromPropertyNotNull")

val IDENTIFIER_FROM_VALUE = Name.identifier("fromValue")

val IDENTIFIER_FROM_EXPRESSION = Name.identifier("fromExpression")

val IDENTIFIER_TO = Name.identifier("to")

val IDENTIFIER_TRANSFORM = Name.identifier("transform")

val IDENTIFIER_VIA = Name.identifier("via")

val PACKAGE_TECH_MAPPIE_API = FqName("tech.mappie.api")

val PACKAGE_TECH_MAPPIE_API_CONFIG = FqName("tech.mappie.api.config")

val CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappingConstructor"))
