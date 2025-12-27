package tech.mappie.testing.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class UniqueGeneratedMapperTest : MappieTestCase() {

    // local model for InnerAddress DTOs
    data class AddressInfo(
        val zipCode: String? = null,
        val city: String? = null,
        val street: String? = null,
        val country: String? = null,
        val location: LocationInfo? = null,
    )

    // local model for ContactData DTOs
    data class ContactDataInfo(
        val email: String? = null,
        val address: AddressInfo? = null
    )

    // local model for Project DTOs
    data class ProjectInfo(
        val id: String,
        val address: AddressInfo,
        val contactData: ContactDataInfo
    )

    // local model for CoordinatesDto
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double,
    ) {
        fun toGeoUri(): String = "geo:0,0?q=$latitude,$longitude"
    }

    data class CoordinatesDto(
        val longitude: Double,
        val latitude: Double
    )

    data class GetProjectResult(
        val id: String,
        val address: GetProjectResultInnerAddress,
        val contactData: GetProjectResultInnerContactData
    )

    data class GetProjectResultInnerAddress(
        val street: String? = null,
        val zipCode: String? = null,
        val city: String? = null,
        val country: String? = null,
        val location: CoordinatesDto? = null
    )

    data class GetProjectResultInnerContactData (
        val address: GetProjectResultInnerAddress,
        val email: String,
    )

    data class GetProjectListResultItem (
        val id: String,
        val address: GetProjectListResultItemInnerAddress,
        val contactData: GetProjectListResultItemInnerContactData
    )

    data class GetProjectListResultItemInnerAddress (
        val street: String? = null,
        val zipCode: String? = null,
        val city: String? = null,
        val country: String? = null,
        val location: CoordinatesDto? = null
    )

    data class GetProjectListResultItemInnerContactData (
        val address: GetProjectListResultItemInnerAddress,
        val email: String,
    )

    /*
        This test fails because the compilation fails.
        IllegalStateException: Internal Mappie error: Cannot access property of
         IrMappieGeneratedClass "GetProjectResultInnerAddressToAddressInfoMapper".

        All data classes have the same property names, and their types should be compatible. The error message
        is confusing.
    */
    @Test
    fun `two implicit mappie objects`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.UniqueGeneratedMapperTest.*

                class Mapper1: ObjectMappie<GetProjectResult, ProjectInfo>()
                class Mapper2: ObjectMappie<GetProjectListResultItem, ProjectInfo>()
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }

    /*
        This test compiles successfully after defining the object mappies explicitly, which is pretty weird.
        It only succeeds when defining both mappings, though.
    */
    @Test
    fun `two mappie objects with helper mappie`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.UniqueGeneratedMapperTest.*

                class Mapper1: ObjectMappie<GetProjectResult, ProjectInfo>()
                class Mapper2: ObjectMappie<GetProjectListResultItem, ProjectInfo>()

                object GetProjectResultInnerAddressToAddressInfoMapper : ObjectMappie<GetProjectResultInnerAddress, AddressInfo>()
                object GetProjectListResultItemInnerAddressToAddressInfoMapper: ObjectMappie<GetProjectListResultItemInnerAddress, AddressInfo>()
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper1 = objectMappie<GetProjectResult, ProjectInfo>("Mapper1")
            val mapper2 = objectMappie<GetProjectListResultItem, ProjectInfo>("Mapper2")

            val addressInfo = AddressInfo("zipCode", "city", "street", "country", LocationInfo(2.0, 1.0))
            val contactDataInfo = ContactDataInfo("test@test.com", addressInfo)
            val projectInfo = ProjectInfo("id", addressInfo, contactDataInfo)

            val addressDto1 = GetProjectResultInnerAddress("street", "zipCode", "city", "country", CoordinatesDto(1.0, 2.0))
            val contactDataDto1 = GetProjectResultInnerContactData(addressDto1, "test@test.com")
            val from1 = GetProjectResult("id", addressDto1, contactDataDto1)

            val addressDto2 = GetProjectListResultItemInnerAddress("street", "zipCode", "city", "country", CoordinatesDto(1.0, 2.0))
            val contactDataDto2 = GetProjectListResultItemInnerContactData(addressDto2, "test@test.com")
            val from2 = GetProjectListResultItem("id", addressDto2, contactDataDto2)

            assertThat(mapper1.map(from1))
                .isEqualTo(projectInfo)
            assertThat(mapper2.map(from2))
                .isEqualTo(projectInfo)
        }
    }
}