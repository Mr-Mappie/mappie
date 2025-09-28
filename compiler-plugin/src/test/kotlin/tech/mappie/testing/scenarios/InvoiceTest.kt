package tech.mappie.testing.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal
import java.time.Instant

class InvoiceTest : MappieTestCase() {

    data class EventMetadataDto(
        val eventTimestamp: Instant,
        val eventSource: String? = null,
    )

    data class InvoiceDto(
        val invoiceNumber: String,
        val items: List<InvoiceLineItemDto>? = null,
    )

    data class WeightDetailsDto(
        val grossWeight: BigDecimal? = null,
        val unitOfWeight: String? = null,
    )

    data class InvoiceLineItemDto(
        val invoiceItemNumber: String,
        val weightDetails: WeightDetailsDto? = null,
    )

    data class InvoiceLineItem(
        val invoiceLineItemId: Int = 0,
        val invoiceItemNumber: String,
        val weightDetails: WeightDetails? = null,
    )

    data class WeightDetails(
        val grossWeight: BigDecimal,
        val unitOfWeight: String,
    )

    data class EventMetadata(
        val eventTimestamp: Instant = Instant.EPOCH,
        val eventSource: String? = null,
    )

    data class Invoice(
        val invoiceId: Int = 0,
        val invoiceNumber: String = "",
        val items: MutableList<InvoiceLineItem> = mutableListOf(),
        val eventMetadata: EventMetadata,
    )

    @Test
    fun `map InvoiceDto to Invoice`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.scenarios.InvoiceTest.*
                import java.math.BigDecimal

                class Mapper : ObjectMappie2<InvoiceDto, EventMetadataDto, Invoice>() {
                    override fun map(first: InvoiceDto, second: EventMetadataDto) = mapping {
                        to::eventMetadata fromValue kotlin.run { EventMetaDataMapper.map(second) }
                        to::items fromProperty first::items transform { 
                            it?.let { InvoiceLineMapper.mapList(it).toMutableList() } ?: mutableListOf() 
                        }
                    }
                }

                object InvoiceLineMapper : ObjectMappie<InvoiceLineItemDto, InvoiceLineItem>()

                object WeightDetailsMapper : ObjectMappie<WeightDetailsDto, WeightDetails>() {
                    override fun map(from: WeightDetailsDto) = mapping {
                        to::grossWeight fromProperty from::grossWeight transform { it ?: BigDecimal.ZERO }
                        to::unitOfWeight fromProperty from::unitOfWeight transform { it ?: "" }
                    }
                }

                object EventMetaDataMapper : ObjectMappie<EventMetadataDto, EventMetadata>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie2<InvoiceDto, EventMetadataDto, Invoice>()
            assertThat(mapper.map(InvoiceDto("10"),EventMetadataDto(Instant.EPOCH)))
                .isEqualTo(Invoice(0, "10", mutableListOf(), EventMetadata()))
        }
    }
}