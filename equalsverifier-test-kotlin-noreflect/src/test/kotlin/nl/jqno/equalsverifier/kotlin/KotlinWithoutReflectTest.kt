package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import org.junit.jupiter.api.Test
import java.util.*

class KotlinWithoutReflectTest {

  /*
   * Normal
   */

  @Test
  fun `Can test Kotlin class with no delegate`() {
    EqualsVerifier.forClass(Normal::class.java).verify()
  }

  @Test
  fun `Can test Kotlin class with no delegate and using withIgnoredFields`() {
    EqualsVerifier.forClass(NormalWithIgnoredField::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Can test Kotlin class with no delegate and using withOnlyTheseFields`() {
    EqualsVerifier.forClass(NormalWithIgnoredField::class.java)
      .withOnlyTheseFields("bar")
      .verify()
  }

  @Test
  fun `Can test Kotlin class with no delegate and using withPrefabValuesForField`() {
    EqualsVerifier.forClass(Normal::class.java)
      .withPrefabValuesForField("foo", 42, 1337)
      .verify()
  }

  /*
   * Interface delegation
   */

  @Test
  fun `Can test Kotlin class with interface delegate`() {
    EqualsVerifier.forClass(InterfaceDelegation::class.java).verify()
  }

  @Test
  fun `Can test Kotlin class with interface delegate exluded by bytecode name via withIgnoredFields`() {
    EqualsVerifier.forClass(InterfaceDelegationWithIgnoredField::class.java)
      .withIgnoredFields("\$\$delegate_0")
      .verify()
  }

  /*
   * Lazy delegation
   */

  @Test
  fun `Gives clear error message with lazy delegate`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(LazyDelegation::class.java).verify() }
      .assertFailure()
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate excluded via withIgnoredFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate included via withOnlyTheseFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withOnlyTheseFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate withPrefabValuesForField`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withPrefabValuesForField("foo", lazy { 42 }, lazy { 1337 })
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate withPrefabValuesForField overload`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withPrefabValuesForField("foo", lazy { 42 }, lazy { 1337 }, lazy { 42 })
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate excluded by bytecode name via withIgnoredFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withIgnoredFields("foo\$delegate")
          .verify()
      }
      .assertFailure()
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate included by bytecode name via withOnlyTheseFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withOnlyTheseFields("foo\$delegate")
          .verify()
      }
      .assertFailure()
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate withPrefabValuesForField by bytecode name`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withPrefabValuesForField("foo\$delegate", lazy { 42 }, lazy { 1337 })
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with lazy delegate withPrefabValuesForField (overload) by bytecode name`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withPrefabValuesForField("foo\$delegate", lazy { 42 }, lazy { 1337 }, lazy { 42 })
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  /*
   * Object delegation
   */

  @Test
  fun `Can test Kotlin class with object delegate`() {
    EqualsVerifier.forClass(ObjectDelegation::class.java).verify()
  }

  @Test
  fun `Gives clear error message with object delegate excluded via withIgnoredFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Can test Kotlin class with object delegate excluded by bytecode name via withIgnoredFields`() {
    EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
      .withIgnoredFields("foo\$receiver")
      .verify()
  }

  @Test
  fun `Gives clear error message with object delegate included via withOnlyTheseFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(ObjectDelegationWithOnlyThisField::class.java)
          .withOnlyTheseFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Can test Kotlin class with object delegate included by bytecode name via withOnlyTheseFields`() {
    EqualsVerifier.forClass(ObjectDelegationWithOnlyThisField::class.java)
      .withOnlyTheseFields("foo\$receiver")
      .verify()
  }

  @Test
  fun `Gives clear error message with object delegate withPrefabValuesForField`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(ObjectDelegation::class.java)
          .withPrefabValuesForField("foo", IntContainer(42), IntContainer(1337))
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Can test Kotlin class with object delegate withPrefabValuesForField by bytecode name`() {
    EqualsVerifier.forClass(ObjectDelegation::class.java)
      .withPrefabValuesForField("foo\$receiver", IntContainer(42), IntContainer(1337))
      .verify()
  }

  /*
   * Member delegation
   */

  @Test
  fun `Can test Kotlin class with member delegate`() {
    EqualsVerifier.forClass(MemberDelegation::class.java).verify()
  }

  @Test
  fun `Gives clear error message with excluded member delegate via withIgnoredFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(MemberDelegation::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with included member delegate via withOnlyTheseFields`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(MemberDelegation::class.java)
          .withOnlyTheseFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  @Test
  fun `Gives clear error message with member delegate withPrefabValuesForField`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(MemberDelegation::class.java)
          .withPrefabValuesForField("foo", 42, 1337)
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.ERROR_MESSAGE)
  }

  /*
   * Helper classes
   */

  class Normal(val foo: Int, val bar: String) {

    override fun equals(other: Any?): Boolean =
      (other is Normal) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }

  class NormalWithIgnoredField(val foo: Int, val bar: String) {

    override fun equals(other: Any?): Boolean =
      (other is NormalWithIgnoredField) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(bar)
  }

  interface Interface {
    val foo: Int
  }

  data class InterfaceImpl(override val foo: Int) : Interface

  class InterfaceDelegation(fooValue: Int) : Interface by InterfaceImpl(fooValue) {

    override fun equals(other: Any?): Boolean =
      (other is InterfaceDelegation) && foo == other.foo

    override fun hashCode(): Int = foo
  }

  class InterfaceDelegationWithIgnoredField(fooValue: Int, val bar: String) : Interface by InterfaceImpl(fooValue) {

    override fun equals(other: Any?): Boolean =
      (other is InterfaceDelegationWithIgnoredField) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(bar)
  }

  class LazyDelegation(fooValue: Int, val bar: String) {
    val foo: Int by lazy { fooValue }

    override fun equals(other: Any?): Boolean =
      (other is LazyDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }

  data class IntContainer(val foo: Int)

  class ObjectDelegation(container: IntContainer, val bar: String) {
    val foo: Int by container::foo

    override fun equals(other: Any?): Boolean =
      (other is ObjectDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }

  class ObjectDelegationWithIgnoredField(container: IntContainer, val bar: String) {
    val foo: Int by container::foo

    override fun equals(other: Any?): Boolean =
      (other is ObjectDelegationWithIgnoredField) && bar == other.bar

    override fun hashCode(): Int = Objects.hash(bar)
  }

  class ObjectDelegationWithOnlyThisField(container: IntContainer, val bar: String) {
    val foo: Int by container::foo

    override fun equals(other: Any?): Boolean =
      (other is ObjectDelegationWithOnlyThisField) && foo == other.foo

    override fun hashCode(): Int = Objects.hash(foo)
  }

  class MemberDelegation(val fooValue: Int, val bar: String) {
    val foo: Int by this::fooValue

    override fun equals(other: Any?): Boolean =
      (other is MemberDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }
}
