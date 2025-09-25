package nl.jqno.equalsverifier.kotlin

import java.util.Objects

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import org.junit.jupiter.api.Test

class KotlinFieldNamesOutputTest {
  @Test
  fun `prints correct field name for straight field in significant field check`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(UnusedFoo::class.java).verify() }
      .assertFailure()
      .assertMessageContains("hashCode relies on foo, but equals does not")
  }

  @Test
  fun `prints correct field name for delegated field in significant field check`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(UnusedDelegatedFoo::class.java).verify() }
      .assertFailure()
      .assertMessageContains("hashCode relies on foo, but equals does not")
  }

  data class StringContainer(val foo: String)

  class UnusedFoo(val foo: String, val bar: Int) {

    override fun equals(other: Any?): Boolean =
      (other is UnusedFoo) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }

  class UnusedDelegatedFoo(container: StringContainer, val bar: Int) {
    val foo: String by container::foo

    override fun equals(other: Any?): Boolean =
      (other is UnusedDelegatedFoo) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }
}
