package nl.jqno.equalsverifier.internal.reflection

import nl.jqno.equalsverifier.internal.reflection.FieldProbe
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

class KotlinFieldProbeTest {

  @Test
  fun isKotlinDelegate() {
    var f = FooContainer::class.java.getDeclaredField("\$\$delegate_0")
    var probe = FieldProbe.of(f)
    assertThat(probe.isKotlinDelegate()).isTrue()
  }

  @Test
  fun isNotKotlinDelegate() {
    var f = FooContainer::class.java.getDeclaredField("bar")
    var probe = FieldProbe.of(f)
    assertThat(probe.isKotlinDelegate()).isFalse()
  }

  interface Foo {
    val foo: Int
  }

  data class FooImpl(override val foo: Int): Foo

  class FooContainer(fooValue: Int, val bar: Int): Foo by FooImpl(fooValue)
}
