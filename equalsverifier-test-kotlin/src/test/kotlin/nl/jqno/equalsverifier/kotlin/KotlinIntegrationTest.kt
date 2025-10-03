package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import java.util.*

class KotlinIntegrationTest {
  @Test
  fun `super's backing field does not affect implementing data class`() {
    EqualsVerifier.forClass(ImplementingDataClass::class.java)
      .withIgnoredFields("base")
      .verify()
  }

  @Test
  fun `super's backing field does not affect implementing regular class`() {
    EqualsVerifier.forClass(ImplementingRegularClass::class.java)
      .verify()
  }

  @Test
  fun `super's backing field does not affect implementing regular class when there's a precondition and withPrefabValuesForField is given`() {
    EqualsVerifier.forClass(ImplementingPreconditionClass::class.java)
      .withPrefabValuesForField("toOverride", 1337, 1338)
      .verify()
  }

  sealed class Base(
    internal open val base: Int,
    internal open val toOverride: Int,
  )

  data class ImplementingDataClass(override val toOverride: Int) : Base(42, toOverride)

  class ImplementingRegularClass(override val toOverride: Int) : Base(42, toOverride) {
    override fun equals(other: Any?): Boolean {
      return other is ImplementingRegularClass
        && base == other.base
        && toOverride == other.toOverride
    }

    override fun hashCode(): Int {
      return Objects.hash(base, toOverride)
    }
  }

  class ImplementingPreconditionClass(override val toOverride: Int) : Base(42, toOverride) {
    override fun equals(other: Any?): Boolean {
      if (toOverride < 1337) throw IllegalStateException("toOverride is smaller than 1337: $toOverride")
      return other is ImplementingPreconditionClass
        && base == other.base
        && toOverride == other.toOverride
    }

    override fun hashCode(): Int {
      return Objects.hash(base, toOverride)
    }
  }
}
