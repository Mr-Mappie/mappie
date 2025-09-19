package tech.mappie.ir.reporting

import org.jetbrains.kotlin.types.Variance
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.IrTestFixtures.createIrClass
import tech.mappie.testing.IrTestFixtures.createIrSimpleType
import tech.mappie.testing.IrTestFixtures.createIrTypeParameter

class PrettyPrinterTest {

     @Test
     fun `visitTypeParameter without super types prints only name`() {
         val tp = createIrTypeParameter("T")
         assertThat(tp.pretty()).isEqualTo("T")
     }

     @Test
     fun `visitTypeParameter with OUT variance includes out and name`() {
         val tp = createIrTypeParameter("T", variance = Variance.OUT_VARIANCE)
         assertThat(tp.pretty()).isEqualTo("out T")
     }

     @Test
     fun `visitTypeParameter with IN variance includes in and name`() {
         val tp = createIrTypeParameter("T", variance = Variance.IN_VARIANCE)
         assertThat(tp.pretty()).isEqualTo("in T")
     }

     @Test
     fun `visitTypeParameter with reified includes reified and name`() {
         val tp = createIrTypeParameter("T", isReified = true)
         assertThat(tp.pretty()).isEqualTo("reified T")
     }

     @Test
     fun `visitTypeParameter prints super types after colon`() {
         val tp = createIrTypeParameter("T")
         val clazz = createIrClass("S")
         tp.superTypes = listOf(
             createIrSimpleType(clazz),
         )
         assertThat(tp.pretty()).isEqualTo("T : S")
     }

    @Test
    fun `visitTypeParameter with multiple super types joins them with comma`() {
        val tp = createIrTypeParameter("T")
        val s = createIrClass("S")
        val u = createIrClass("U")
        tp.superTypes = listOf(
            createIrSimpleType(s),
            createIrSimpleType(u),
        )
        assertThat(tp.pretty()).isEqualTo("T : S, U")
    }
}
