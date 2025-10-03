package nl.jqno.equalsverifier.internal.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.javaField

class KotlinFieldProbeTest {
  @Test
  fun `getDisplayName displays normal field name`() {
    val f = Container::normal.javaField
    val probe = FieldProbe.of(f)
    assertThat(probe.getDisplayName()).isEqualTo("normal")
  }

  @Test
  fun `getDisplayName displays delegate field name`() {
    val f = Container::delegate.javaField
    val probe = FieldProbe.of(f)
    assertThat(probe.getDisplayName()).isEqualTo("delegate")
    assertThat(probe.getName()).isEqualTo("delegate\$delegate")
  }

  class Container {
    val normal: Int = 1
    val delegate: Int by lazy { 1 }
  }
}
