package nl.jqno.equalsverifier.internal.reflection.kotlin

import nl.jqno.equalsverifier.EqualsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.javaField

class KotlinScreenTest {

  @Test
  fun canProbe() {
    // We're in a Maven module with kotlin-reflect
    assertThat(KotlinScreen.canProbe()).isTrue()
  }

  @Test
  fun lazyIsLazy() {
    assertThat(KotlinScreen.LAZY).isEqualTo(Lazy::class.java)
  }

  @Test
  fun isKotlin() {
    assertThat(KotlinScreen.isKotlin(KotlinScreenTest::class.java)).isTrue()
  }

  @Test
  fun isNotKotlin() {
    assertThat(KotlinScreen.isKotlin(EqualsVerifier::class.java)).isFalse()
  }

  @Test
  fun makeLazy() {
    val k = KotlinScreen.lazy(42)
    assertThat(Lazy::class.java.isAssignableFrom(k.javaClass)).isTrue()
  }

  @Test
  fun isSyntheticKotlinDelegate() {
    val f = FooContainer::class.java.getDeclaredField("\$\$delegate_0")
    assertThat(KotlinScreen.isSyntheticKotlinDelegate(f)).isTrue()
  }

  @Test
  fun isNotSyntheticKotlinDelegate() {
    val f = FooContainer::bar.javaField
    assertThat(KotlinScreen.isSyntheticKotlinDelegate(f)).isFalse()
  }

  @Test
  fun isLazy() {
    val f = LazyContainer::class.java.getDeclaredField("foo\$delegate")
    assertThat(KotlinScreen.isKotlinLazy(f)).isTrue()
  }

  @Test
  fun isNotLazy() {
    val f = LazyContainer::nonLazy.javaField
    assertThat(KotlinScreen.isKotlinLazy(f)).isFalse()
  }

  interface Foo {
    val foo: Int
  }

  data class FooImpl(override val foo: Int): Foo

  class FooContainer(fooValue: Int, val bar: Int): Foo by FooImpl(fooValue)

  class LazyContainer(val nonLazy: Int) {
    val foo: Int by lazy { nonLazy }
  }
}
