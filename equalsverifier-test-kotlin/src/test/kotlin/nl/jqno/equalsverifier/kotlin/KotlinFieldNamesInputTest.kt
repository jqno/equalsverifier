package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import java.util.*

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
  fun `withIgnoredFields - delegate double field with its Kotlin name`() {
    EqualsVerifier.forClass(UnusedDelegatedDouble::class.java)
      .withIgnoredFields(UnusedDelegatedDouble::baz.name)
      .verify()
  }

  @Test
  fun `withIgnoredFields - delegate double field with its bytecode name`() {
    EqualsVerifier.forClass(UnusedDelegatedDouble::class.java)
      .withIgnoredFields("baz\$receiver")
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

  @Test
  fun `withOnlyTheseFields - delegate double field with its Kotlin name`() {
    EqualsVerifier.forClass(UnusedDelegatedDouble::class.java)
      .withOnlyTheseFields(UnusedDelegatedDouble::quux.name)
      .verify()
  }

  @Test
  fun `withOnlyTheseFields - delegate double field with its bytecode name`() {
    EqualsVerifier.forClass(UnusedDelegatedDouble::class.java)
      .withOnlyTheseFields("quux\$receiver")
      .verify()
  }

  @Test
  fun `withPrefabValuesForField - normal field`() {
    EqualsVerifier.forClass(Precondition::class.java)
      .withPrefabValuesForField(Precondition::foo.name, "foo1", "foo2")
      .verify()
  }

  @Test
  fun `withPrefabValuesForField - delegate field with its Kotlin name`() {
    EqualsVerifier.forClass(PreconditionDelegated::class.java)
      .withPrefabValuesForField(PreconditionDelegated::foo.name, StringContainer("foo1"), StringContainer("foo2"))
      .verify()
  }

  @Test
  fun `withPrefabValuesForField - delegate field with its bytecode name`() {
    EqualsVerifier.forClass(PreconditionDelegated::class.java)
      .withPrefabValuesForField("foo\$receiver", StringContainer("foo1"), StringContainer("foo2"))
      .verify()
  }

  @Test
  fun `withPrefabValuesForField - lazy field with its Kotlin name`() {
    EqualsVerifier.forClass(PreconditionLazy::class.java)
      .withPrefabValuesForField(PreconditionLazy::foo.name, lazy { "foo1" }, lazy { "foo2" })
      .verify()
  }

  @Test
  fun `withPrefabValuesForField - lazy field with its bytecode name`() {
    EqualsVerifier.forClass(PreconditionLazy::class.java)
      .withPrefabValuesForField("foo\$delegate", lazy { "foo1" }, lazy { "foo2" })
      .verify()
  }

  data class StringContainer(val foo: String)
  data class IntContainer(val bar: Int)
  data class DoubleContainer(val foo: String, val bar: Int)

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

  class UnusedDelegatedDouble(container: DoubleContainer) {
    val baz: String by container::foo
    val quux: Int by container::bar

    override fun equals(other: Any?): Boolean =
      (other is UnusedDelegatedDouble) && quux == other.quux

    override fun hashCode(): Int = Objects.hash(quux)
  }

  class Precondition(val foo: String) {

    override fun equals(other: Any?): Boolean {
      require(foo.startsWith("foo")) { "foo must start with 'foo' but was '$foo'" }
      return (other is Precondition) && foo == other.foo
    }

    override fun hashCode(): Int = Objects.hash(foo)
  }

  class PreconditionDelegated(container: StringContainer) {
    val foo: String by container::foo

    override fun equals(other: Any?): Boolean {
      require(foo.startsWith("foo")) { "foo must start with 'foo' but was '$foo'" }
      return (other is PreconditionDelegated) && foo == other.foo
    }

    override fun hashCode(): Int = Objects.hash(foo)
  }

  class PreconditionLazy(fooValue: String) {
    val foo: String by lazy { fooValue }

    override fun equals(other: Any?): Boolean {
      require(foo.startsWith("foo")) { "foo must start with 'foo' but was '$foo'" }
      return (other is PreconditionLazy) && foo == other.foo
    }

    override fun hashCode(): Int = Objects.hash(foo)
  }
}
