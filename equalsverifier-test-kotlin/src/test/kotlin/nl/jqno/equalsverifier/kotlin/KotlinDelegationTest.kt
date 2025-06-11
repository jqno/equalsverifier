package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class KotlinDelegationTest {

  @Test
  fun `succeed when class uses interface delegation`() {
    EqualsVerifier.forClass(FooContainer::class.java).verify()
  }

  interface Foo {
    val foo: Int
  }

  data class FooImpl(override val foo: Int): Foo

  class FooContainer(fooValue: Int): Foo by FooImpl(fooValue) {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is FooContainer) return false
      return foo == other.foo
    }

    override fun hashCode(): Int = foo
  }
}
