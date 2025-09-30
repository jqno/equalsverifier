package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier_testhelpers.ExpectedException
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;
import org.junit.jupiter.api.Test
import java.util.Objects

class KotlinWithoutReflectTest {
  private val REQUIRED = "required to verify this class"
  private val DELEGATE = "is a delegate field"

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
      .assertMessageContains(KotlinScreen.GAV, REQUIRED)
  }

  @Test
  fun `Gives clear error message with ignored lazy delegate`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.GAV, DELEGATE)
  }

  @Test
  fun `Gives clear error message with lazy delegates ignored by bytecode name`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(LazyDelegation::class.java)
          .withIgnoredFields("foo\$delegate")
          .verify()
      }
      .assertFailure()
      .assertMessageContains(KotlinScreen.GAV, REQUIRED)
  }

  @Test
  fun `Can test Kotlin class with object delegate`() {
    EqualsVerifier.forClass(ObjectDelegation::class.java).verify()
  }

  @Test
  fun `Gives clear error message with ignored object delegate`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.GAV, DELEGATE)
  }

  @Test
  fun `Can test Kotlin class with object delegate ignored by bytecode name`() {
    EqualsVerifier.forClass(ObjectDelegationWithIgnoredField::class.java)
      .withIgnoredFields("foo\$receiver")
      .verify()
  }

  @Test
  fun `Can test Kotlin class with member delegate`() {
    EqualsVerifier.forClass(MemberDelegation::class.java).verify()
  }

  @Test
  fun `Gives clear error message with ignored member delegate`() {
    ExpectedException
      .`when` {
        EqualsVerifier.forClass(MemberDelegation::class.java)
          .withIgnoredFields("foo")
          .verify()
      }
      .assertThrows(IllegalStateException::class.java)
      .assertMessageContains(KotlinScreen.GAV, DELEGATE)
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
