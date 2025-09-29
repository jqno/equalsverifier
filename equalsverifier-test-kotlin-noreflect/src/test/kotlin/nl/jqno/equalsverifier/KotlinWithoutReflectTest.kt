package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinWithoutReflectTest {
  @Test
  fun `Can test Kotlin class with no delegates`() {
    EqualsVerifier.forClass(Normal::class.java).verify()
  }

  @Test
  fun `Can test Kotlin class with no delegates with ignored field`() {
    EqualsVerifier.forClass(Normal::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with delegates`() {
    EqualsVerifier.forClass(LazyDelegation::class.java).verify()
  }

  @Test
  fun `Gives clear error message with ignored delegates`() {
    EqualsVerifier.forClass(LazyDelegation::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Can test Kotlin class with delegate when it's ignored with bytecode name`() {
    EqualsVerifier.forClass(LazyDelegation::class.java)
      .withIgnoredFields("foo\$delegate")
      .verify()
  }

  class Normal(val foo: Int, val bar: String) {

    override fun equals(other: Any?): Boolean =
      (other is Normal) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = (31 * foo) + bar.hashCode()
  }

  class LazyDelegation(fooValue: Int, val bar: String) {
    val foo: Int by lazy { fooValue }

    override fun equals(other: Any?): Boolean =
      (other is LazyDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = (31 * foo) + bar.hashCode()
  }
}
