package nl.jqno.equalsverifier.internal.reflection.kotlin

import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

class KotlinScreenTest {

  @Test
  fun isSyntheticKotlinDelegate() {
    val f = FooContainer::class.java.getDeclaredField("\$\$delegate_0")
    assertThat(KotlinScreen.isSyntheticKotlinDelegate(f)).isTrue()
  }

  @Test
  fun isNotSyntheticKotlinDelegate() {
    val f = FooContainer::class.java.getDeclaredField("bar")
    assertThat(KotlinScreen.isSyntheticKotlinDelegate(f)).isFalse()
  }

  interface Foo {
    val foo: Int
  }

  data class FooImpl(override val foo: Int): Foo

  class FooContainer(fooValue: Int, val bar: Int): Foo by FooImpl(fooValue)
}
