package tech.mappie.testing.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class EnumMappieStarProjection : MappieTestCase() {

    enum class ProjectAndTaskStatus(val value: Int) {
        New(0),
        Open(1),
        InProgress(2),
        Finished(3),
        Complaint(4);
    }

    enum class ProjectStatus(val stringResId: String) {
        NEW("Res.string.project_status_new"),
        OPEN("Res.string.project_status_open"),
        IN_PROGRESS("Res.string.project_status_in_progress"),
        COMPLAINT("Res.string.project_status_complaint"),
        FINISHED("Res.string.project_status_finished");
    }

    @Test
    fun `map ProjectAndTaskStatus and ProjectStatus`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.scenarios.EnumMappieStarProjection.*
                import kotlin.enums.EnumEntries

                class ProjectStatusDtoToInfoMapper : BaseEnumMapper<ProjectAndTaskStatus, ProjectStatus>(ProjectStatus.entries)
                
                class ProjectStatusInfoToDtoMapper : BaseEnumMapper<ProjectStatus, ProjectAndTaskStatus>(ProjectAndTaskStatus.entries)
                
                abstract class BaseEnumMapper<F: Enum<F>, T: Enum<T>>(
                    private val targetValues: EnumEntries<T>,
                ) : EnumMappie<F, T>() {
                    override fun map(from: F): T {
                        val sourceCleaned = from.name.lowercase()
                            .replace(
                                "_",
                                ""
                            )
                        return targetValues.first { target ->
                            target.name.lowercase()
                                .replace(
                                    "_",
                                    ""
                                ) == sourceCleaned
                        }
                    }
                }
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            enumMappie<ProjectAndTaskStatus, ProjectStatus>("ProjectStatusDtoToInfoMapper").let { mapper ->
                assertThat(mapper.map(ProjectAndTaskStatus.InProgress))
                    .isEqualTo(ProjectStatus.IN_PROGRESS)
            }

            enumMappie<ProjectStatus, ProjectAndTaskStatus>("ProjectStatusInfoToDtoMapper").let { mapper ->
                assertThat(mapper.map(ProjectStatus.IN_PROGRESS))
                    .isEqualTo(ProjectAndTaskStatus.InProgress)
            }
        }
    }
}