package tech.mappie.testing.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.Instant

class PaginationTest : MappieTestCase() {

    data class GetProjectListResult (
        val items: List<GetProjectListResultItem>,
        val totalCount: Int,
        val page: Int,
        val pageSize: Int,
        val totalPages: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean
    )

    data class GetProjectListResultItem (
        val createdAt: Instant,
        val lastModifiedAt: Instant? = null,
        val id: String,
        val number: String
    )

    data class PaginationInfo<T>(
        val pageKey: Int,
        val totalPages: Int,
        val isLastPage: Boolean,
        val items: List<T>,
        val totalItems: Int,
    ) {
        val nextPageKey: Int
            get() = pageKey + 1
    }

    data class ProjectListItem(
        val id: String,
        val number: String,
        val date: Instant
    )

    @Test
    fun `mapper without pagination`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.PaginationTest.*

                class ProjectListItemMapper: ObjectMappie<GetProjectListResultItem, ProjectListItem>() {
                    override fun map(from: GetProjectListResultItem): ProjectListItem = mapping {
                        to::date fromProperty from::createdAt
                    }
                }

                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<GetProjectListResultItem, ProjectListItem>("ProjectListItemMapper")

            assertThat(mapper.map(GetProjectListResultItem(Instant.MIN, null, "id", "number")))
                .isEqualTo(ProjectListItem("id", "number", Instant.MIN))
        }
    }

    @Test
    fun `mapper with pagination`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.PaginationTest.*

                class GetProjectListResultMapper: ObjectMappie<GetProjectListResult, PaginationInfo<ProjectListItem>>() {
                    override fun map(from: GetProjectListResult) = mapping {
                        to::pageKey fromProperty from::page
                        to::isLastPage fromValue !from.hasNextPage
                        to::totalItems fromProperty from::totalPages  
                    }
                }

                object ProjectListItemMapper: ObjectMappie<GetProjectListResultItem, ProjectListItem>() {
                    override fun map(from: GetProjectListResultItem): ProjectListItem = mapping {
                        to::date fromProperty from::createdAt
                    }
                }

                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<GetProjectListResult, PaginationInfo<ProjectListItem>>("GetProjectListResultMapper")

            val input = GetProjectListResult(
                listOf(GetProjectListResultItem(Instant.MIN, null, "id", "number")),
                1,
                2,
                3,
                4,
                false,
                false
            )

            assertThat(mapper.map(input))
                .isEqualTo(PaginationInfo(2, 4, true, listOf(ProjectListItem("id", "number", Instant.MIN)), 4))
        }
    }
}
