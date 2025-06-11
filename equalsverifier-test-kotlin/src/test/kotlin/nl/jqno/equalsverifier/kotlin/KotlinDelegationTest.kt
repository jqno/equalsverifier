package nl.jqno.equalsverifier.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class KotlinDelegationTest {

  @Test
  fun `succeed when class uses interface delegation`() {
    EqualsVerifier.forClass(FooBarImpl::class.java).verify()
  }

  interface Foo {
    val foo: Int
  }

  interface Bar {
    val bar: Int
  }

  data class BarImpl(override val bar: Int): Bar

  class FooBarImpl(barValue: Int): Foo, Bar by BarImpl(barValue) {

    override val foo = -bar

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is FooBarImpl) return false
      return foo == other.foo
    }

    override fun hashCode(): Int = bar
  }
}
