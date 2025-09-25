package nl.jqno.equalsverifier.internal.reflection.kotlin

import nl.jqno.equalsverifier.internal.reflection.TypeTag
import nl.jqno.equalsverifier_testhelpers.types.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinProbeTest {
  @Test
  fun makeLazy() {
    val k = KotlinProbe.lazy(42)
    assertThat(Lazy::class.java.isAssignableFrom(k.javaClass)).isTrue()
  }

  @Test
  fun `translateKotlinToBytecodeFieldName - translate Java field`() {
    val actual = KotlinProbe.translateKotlinToBytecodeFieldName(Point::class.java, "x")
    assertThat(actual).isEqualTo("x")
  }

  @Test
  fun `translateKotlinToBytecodeFieldName - translate normal field`() {
    val actual = KotlinProbe.translateKotlinToBytecodeFieldName(LazyContainer::class.java, "nonLazy")
    assertThat(actual).isEqualTo("nonLazy")
  }

  @Test
  fun `translateKotlinToBytecodeFieldName - translate delegate field`() {
    val actual = KotlinProbe.translateKotlinToBytecodeFieldName(LazyContainer::class.java, "foo")
    assertThat(actual).isEqualTo("foo\$delegate")
  }

  @Test
  fun `translateKotlinToBytecodeFieldName - translate multiple fields`() {
    val names = listOf("foo", "nonLazy")
    val actual = KotlinProbe.translateKotlinToBytecodeFieldNames(LazyContainer::class.java, names)
    assertThat(actual).isEqualTo(listOf("foo\$delegate", "nonLazy"))
  }

  @Test
  fun `getKotlinPropertyNameFor - normal field`() {
    val f = LazyContainer::class.java.getDeclaredField("nonLazy")
    val actual = KotlinProbe.getKotlinPropertyNameFor(f)
    assertThat(actual).contains("nonLazy")
  }

  @Test
  fun `getKotlinPropertyNameFor - lazy`() {
    val f = LazyContainer::class.java.getDeclaredField("foo\$delegate")
    val actual = KotlinProbe.getKotlinPropertyNameFor(f)
    assertThat(actual).contains("foo")
  }

  @Test
  fun `determineLazyType - field doesn't exist`() {
    val fromAnotherClass = NestedGenericLazyContainer::class.java.getDeclaredField("nonLazy")
    val actual = KotlinProbe.determineLazyType(LazyContainer::class.java, fromAnotherClass)
    assertThat(actual).isEmpty()
  }

  @Test
  fun `determineLazyType - not lazy`() {
    val f = LazyContainer::class.java.getDeclaredField("nonLazy")
    val actual = KotlinProbe.determineLazyType(LazyContainer::class.java, f)
    assertThat(actual).isEmpty()
  }

  @Test
  fun `determineLazyType - actually lazy`() {
    val f = LazyContainer::class.java.getDeclaredField("foo\$delegate")
    val actual = KotlinProbe.determineLazyType(LazyContainer::class.java, f)
    assertThat(actual).contains(TypeTag(Lazy::class.java, TypeTag(Int::class.java)))
  }

  @Test
  fun `determineLazyType - nested generics lazy`() {
    val f = NestedGenericLazyContainer::class.java.getDeclaredField("foo\$delegate")
    val actual = KotlinProbe.determineLazyType(NestedGenericLazyContainer::class.java, f)
    assertThat(actual).contains(
      TypeTag(
        Lazy::class.java,
        TypeTag(List::class.java, TypeTag(List::class.java, TypeTag(Int::class.java)))
      )
    )
  }

  class LazyContainer(val nonLazy: Int) {
    val foo: Int by lazy { nonLazy }
  }

  class NestedGenericLazyContainer(val nonLazy: List<List<Int>>) {
    val foo: List<List<Int>> by lazy { nonLazy }
  }
}
