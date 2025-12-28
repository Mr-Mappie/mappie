package tech.mappie.util

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val IDENTIFIER_MAPPING = Name.identifier("mapping")

val IDENTIFIER_MAP = Name.identifier("map")

val IDENTIFIER_MAP_NULLABLE = Name.identifier("mapNullable")

val IDENTIFIER_FROM_ENUM_ENTRY = Name.identifier("fromEnumEntry")

val IDENTIFIER_THROWN_BY_ENUM_ENTRY = Name.identifier("thrownByEnumEntry")

val IDENTIFIER_FROM_PROPERTY = Name.identifier("fromProperty")

val IDENTIFIER_FROM_PROPERTY_NOT_NULL = Name.identifier("fromPropertyNotNull")

val IDENTIFIER_FROM_VALUE = Name.identifier("fromValue")

val IDENTIFIER_FROM_EXPRESSION = Name.identifier("fromExpression")

val IDENTIFIER_TO = Name.identifier("to")

val IDENTIFIER_TRANSFORM = Name.identifier("transform")

val IDENTIFIER_VIA = Name.identifier("via")

val ALL_MAPPING_FUNCTIONS = listOf(
    IDENTIFIER_FROM_ENUM_ENTRY,
    IDENTIFIER_THROWN_BY_ENUM_ENTRY,
    IDENTIFIER_FROM_PROPERTY,
    IDENTIFIER_FROM_PROPERTY_NOT_NULL,
    IDENTIFIER_FROM_VALUE,
    IDENTIFIER_FROM_EXPRESSION,
    IDENTIFIER_TO,
    IDENTIFIER_TRANSFORM,
    IDENTIFIER_VIA,
)

val IDENTIFIER_IDENTITY_MAPPER = Name.identifier("IdentityMapper")

val PACKAGE_KOTLIN = FqName("kotlin")

val PACKAGE_TECH_MAPPIE_API = FqName("tech.mappie.api")

val PACKAGE_TECH_MAPPIE_API_CONFIG = FqName("tech.mappie.api.config")

val CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappingConstructor"))

val CLASS_ID_MULTIPLE_OBJECT_MAPPING_CONSTRUCTOR = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("MultipleObjectMappingConstructor"))

val CLASS_ID_ENUM_MAPPING_CONSTRUCTOR = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("EnumMappingConstructor"))

val CLASS_ID_MAPPIE = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("Mappie"))

val CLASS_ID_ENUM_MAPPIE = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("EnumMappie"))

val CLASS_ID_OBJECT_MAPPIE = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappie"))

val CLASS_ID_OBJECT_MAPPIE2 = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappie2"))

val CLASS_ID_OBJECT_MAPPIE3 = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappie3"))

val CLASS_ID_OBJECT_MAPPIE4 = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappie4"))

val CLASS_ID_OBJECT_MAPPIE5 = ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier("ObjectMappie5"))

val CLASS_ID_USE_DEFAULT_ARGUMENTS = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("UseDefaultArguments"))

val CLASS_ID_USE_STRICT_ENUMS = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("UseStrictEnums"))

val CLASS_ID_USE_STRICT_JAVA_NULLABILITY = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("UseStrictPlatformTypeNullabilityValidation"))

val CLASS_ID_USE_STRICT_VISIBILITY = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("UseStrictVisibility"))

val CLASS_ID_EXCLUDE_FROM_MAPPING = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("ExcludeFromMapping"))

val CLASS_ID_USE_NAMING_CONVENTION = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("UseNamingConvention"))

val CLASS_ID_NAMING_CONVENTION = ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier("NamingConvention"))

val CLASS_ID_RECORD = ClassId(FqName("java.lang"), Name.identifier("Record"))
