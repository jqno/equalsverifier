package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.Objects

class KotlinWithoutReflectTest {
  private val ERROR_MESSAGE = "kotlin-reflect required to verify"

  @Test
  fun `Can test Kotlin class with no delegate`() {
    EqualsVerifier.forClass(Normal::class.java).verify()
  }

  @Test
  fun `Can test Kotlin class with no delegate with ignored field`() {
    EqualsVerifier.forClass(NormalWithIgnoredField::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with lazy delegate`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(LazyDelegation::class.java).verify() }
      .assertFailure()
      .assertMessageContains(ERROR_MESSAGE)
  }

  @Test@Disabled
  fun `Gives clear error message with ignored lazy delegate`() {
    EqualsVerifier.forClass(LazyDelegation::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with lazy delegates ignored by bytecode name`() {
    ExpectedException
      .`when` { EqualsVerifier.forClass(LazyDelegation::class.java).withIgnoredFields("foo\$delegate").verify() }
      .assertFailure()
      .assertMessageContains(ERROR_MESSAGE)
  }

  @Test
  fun `Can test Kotlin class with object delegate`() {
    EqualsVerifier.forClass(ObjectDelegation::class.java).verify()
  }

  @Test@Disabled
  fun `Gives clear error message with ignored object delegate`() {
    EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

  @Test
  fun `Gives clear error message with object delegate ignored by bytecode name`() {
    EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
      .withIgnoredFields("foo\$receiver")
      .verify()
  }

  @Test
  fun `Can test Kotlin class with member delegate`() {
    EqualsVerifier.forClass(MemberDelegation::class.java).verify()
  }

  @Test@Disabled
  fun `Gives clear error message with ignored member delegate`() {
    EqualsVerifier.forClass(MemberDelegation::class.java)
      .withIgnoredFields("foo")
      .verify()
  }

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

  class MemberDelegation(val fooValue: Int, val bar: String) {
    val foo: Int by this::fooValue

    override fun equals(other: Any?): Boolean =
      (other is MemberDelegation) && foo == other.foo && bar == other.bar

    override fun hashCode(): Int = Objects.hash(foo, bar)
  }
}
