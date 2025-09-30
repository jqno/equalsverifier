package nl.jqno.equalsverifier.internal.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinFieldProbeTest {
  @Test
  fun `getDisplayName displays normal field name`() {
    val f = Container::class.java.getDeclaredField("normal")
    val probe = FieldProbe.of(f)
    assertThat(probe.getDisplayName()).isEqualTo("normal")
  }

  @Test
  fun `getDisplayName displays delegate field name`() {
    val f = Container::class.java.getDeclaredField("delegate\$delegate")
    val probe = FieldProbe.of(f)
    assertThat(probe.getDisplayName()).isEqualTo("delegate\$delegate")
    assertThat(probe.getName()).isEqualTo("delegate\$delegate")
  }

  class Container {
    val normal: Int = 1
    val delegate: Int by lazy { 1 }
  }
}
