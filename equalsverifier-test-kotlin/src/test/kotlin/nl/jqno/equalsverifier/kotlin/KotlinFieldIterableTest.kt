package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.internal.reflection.FieldIterable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.Field

class KotlinFieldIterableTest {
  val actual = HashSet<Field>()

  @Test
  fun `FieldIterable ofKotlin ignores superclass backing fields`() {
    actual.addAll(FieldIterable.ofKotlin(ImplementingDataClass::class.java).map { it.getField() })

    assertThat(actual)
      .isEqualTo(
        setOf(
          Base::class.java.getDeclaredField("base"),
          ImplementingDataClass::class.java.getDeclaredField("toOverride")
        )
      )
  }

  @Test
  fun `FieldIterable of does not ignore superclass backing fields`() {
    actual.addAll(FieldIterable.of(ImplementingDataClass::class.java).map { it.getField() })

    assertThat(actual)
      .isEqualTo(
        setOf(
          Base::class.java.getDeclaredField("base"),
          Base::class.java.getDeclaredField("toOverride"),
          ImplementingDataClass::class.java.getDeclaredField("toOverride")
        )
      )
  }

  sealed class Base(
    internal open val base: Int,
    internal open val toOverride: Int,
  )

  data class ImplementingDataClass(override val toOverride: Int) : Base(42, toOverride)
}
