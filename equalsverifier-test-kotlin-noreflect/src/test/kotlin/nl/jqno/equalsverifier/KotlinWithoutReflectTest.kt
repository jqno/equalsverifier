package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import org.junit.jupiter.api.Test

class KotlinWithoutReflectTest {
  private val ERROR_MESSAGE = "kotlin-reflect required to verify"

  @Test
  fun `Can test Kotlin class with no delegates`() {
    EqualsVerifier.forClass(Normal::class.java).verify()
  }

  @Test
  fun `Can test Kotlin class with no delegates with ignored field`() {
    EqualsVerifier.forClass(NormalWithIgnoredField::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with delegates`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(LazyDelegation::class.java).verify() }
      .assertFailure()
      .assertMessageContains(ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with ignored delegates`() {
    EqualsVerifier.forClass(LazyDelegation::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with ignored delegates, even when it's ignored with bytecode name`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(LazyDelegation::class.java).withIgnoredFields("foo\$delegate").verify() }
      .assertFailure()
      .assertMessageContains(ERROR_MESSAGE)
  }

  class Normal(val foo: Int, val bar: String) {

    override fun equals(other: Any?): Boolean =
      (other is Normal) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = (31 * foo) + bar.hashCode()
  }

  class NormalWithIgnoredField(val foo: Int, val bar: String) {

    override fun equals(other: Any?): Boolean =
      (other is NormalWithIgnoredField) && bar == other.bar

    override fun hashCode(): Int = bar.hashCode()
  }

  class LazyDelegation(fooValue: Int, val bar: String) {
    val foo: Int by lazy { fooValue }

    override fun equals(other: Any?): Boolean =
      (other is LazyDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = (31 * foo) + bar.hashCode()
  }
}
