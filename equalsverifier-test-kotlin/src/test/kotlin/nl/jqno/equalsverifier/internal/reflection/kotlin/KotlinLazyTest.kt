package nl.jqno.equalsverifier.internal.reflection.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinLazyTest {

  @Test
  fun makeLazy() {
    val k = KotlinLazy.lazy(42)
    assertThat(Lazy::class.java.isAssignableFrom(k.javaClass)).isTrue()
  }
}
