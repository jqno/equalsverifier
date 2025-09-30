package nl.jqno.equalsverifier.internal.reflection.kotlin

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class KotlinScreenTest {

  @Test
  fun canProbe() {
    // We're in a Maven module without kotlin-reflect
    assertThat(KotlinScreen.canProbe()).isFalse()
  }
}
