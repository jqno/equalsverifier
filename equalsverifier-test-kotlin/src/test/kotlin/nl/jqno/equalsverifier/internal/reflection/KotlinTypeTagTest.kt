package nl.jqno.equalsverifier.internal.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.javaField

class KotlinTypeTagTest {
  @Test
  fun `TypeTag of can find lazy type`() {
    val f = Container::simple.javaField
    val actual = TypeTag.of(f, TypeTag(Container::class.java))
    assertThat(actual).isEqualTo(TypeTag(Lazy::class.java, TypeTag(Int::class.java)))
  }

  @Test
  fun `TypeTag of can find lazy nested generic type`() {
    val f = Container::nestedGenerics.javaField
    val actual = TypeTag.of(f, TypeTag(Container::class.java))
    assertThat(actual).isEqualTo(
      TypeTag(
        Lazy::class.java,
        TypeTag(List::class.java, TypeTag(List::class.java, TypeTag(Int::class.java)))
      )
    )
  }

  class Container {
    val simple: Int by lazy { 1 }
    val nestedGenerics: List<List<Int>> by lazy { listOf() }
  }
}
