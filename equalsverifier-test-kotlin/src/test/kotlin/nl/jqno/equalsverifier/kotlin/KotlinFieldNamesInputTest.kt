package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import java.util.Objects

class KotlinFieldNamesInputTest {
  @Test
  fun `withIgnoredFields - normal field`() {
    EqualsVerifier.forClass(UnusedFoo::class.java)
      .withIgnoredFields(UnusedFoo::foo.name)
      .verify()
  }

  @Test
  fun `withIgnoredFields - delegate field with its Kotlin name`() {
    EqualsVerifier.forClass(UnusedDelegatedFoo::class.java)
      .withIgnoredFields(UnusedDelegatedFoo::foo.name)
      .verify()
  }

  @Test
  fun `withIgnoredFields - delegate field with its bytecode name`() {
    EqualsVerifier.forClass(UnusedDelegatedFoo::class.java)
      .withIgnoredFields("foo\$receiver")
      .verify()
  }

  @Test
  fun `withOnlyTheseFields - normal field`() {
    EqualsVerifier.forClass(UnusedFoo::class.java)
      .withOnlyTheseFields(UnusedFoo::bar.name)
      .verify()
  }

  @Test
  fun `withOnlyTheseFields - delegate field with its Kotlin name`() {
    EqualsVerifier.forClass(UnusedDelegatedFoo::class.java)
      .withOnlyTheseFields(UnusedDelegatedFoo::bar.name)
      .verify()
  }

  @Test
  fun `withOnlyTheseFields - delegate field with its bytecode name`() {
    EqualsVerifier.forClass(UnusedDelegatedFoo::class.java)
      .withOnlyTheseFields("bar\$receiver")
      .verify()
  }

  data class StringContainer(val foo: String)
  data class IntContainer(val bar: Int)

  class UnusedFoo(val foo: String, val bar: Int) {

    override fun equals(other: Any?): Boolean =
      (other is UnusedFoo) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(bar)
  }

  class UnusedDelegatedFoo(stringContainer: StringContainer, intContainer: IntContainer) {
    val foo: String by stringContainer::foo
    val bar: Int by intContainer::bar

    override fun equals(other: Any?): Boolean =
      (other is UnusedDelegatedFoo) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(bar)
  }
}
