package nl.jqno.equalsverifier.kotlin

import kotlin.reflect.KProperty
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class KotlinDelegationTest {

  /*
   * Interface delegation
   */

  @Test
  fun `succeed when class uses interface delegation`() {
    EqualsVerifier.forClass(InterfaceDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields 1`() {
    EqualsVerifier.forClass(TwoFoosDelegation1::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields 2`() {
    EqualsVerifier.forClass(TwoFoosDelegation2::class.java).verify()
  }

  @Test
  fun `succeed when class uses interface delegation with two fields 3`() {
    EqualsVerifier.forClass(TwoFoosDelegation3::class.java).verify()
  }

  /*
   * Lazy delegation
   */

  @Test
  fun `succeed when class uses lazy delegate`() {
    EqualsVerifier.forClass(LazyDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses lazy delegate and has prefab values`() {
    EqualsVerifier.forClass(LazyDelegation::class.java).withPrefabValues(Lazy::class.java, lazy { 1 }, lazy { 2 }).verify()
  }

  @Test
  fun `succeed when class uses lazy generic delegate`() {
    EqualsVerifier.forClass(LazyGenericDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses nested lazy generic delegate`() {
    EqualsVerifier.forClass(NestedLazyGenericDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses two lazy delegates`() {
    EqualsVerifier.forClass(TwoLazyDelegations::class.java).verify()
  }

  @Test
  fun `succeed when class uses two lazy delegates and has generic prefab values`() {
    EqualsVerifier.forClass(TwoLazyDelegations::class.java).withGenericPrefabValues(Lazy::class.java, { lazy { it } }).verify()
  }

  /*
   * Other kinds of delegation
   */

  @Test
  fun `succeed when class uses top level delegation`() {
    EqualsVerifier.forClass(TopLevelDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses member delegation`() {
    EqualsVerifier.forClass(MemberDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses delegation from another class`() {
    EqualsVerifier.forClass(AnotherClassDelegation::class.java).verify()
  }

  @Test
  fun `succeed when class uses map delegation`() {
    EqualsVerifier.forClass(MapDelegation::class.java).withPrefabValues(Map::class.java, mapOf("foo" to "a"), mapOf("foo" to "b")).verify()
  }

  @Test
  fun `succeed when class uses reflection delegation`() {
    EqualsVerifier.forClass(ReflectionDelegation::class.java).verify()
  }
}

interface Foo {
  val foo: Int
}

/*
 * Interface delegation
 */

data class FooImpl(override val foo: Int): Foo

class InterfaceDelegation(fooValue: Int): Foo by FooImpl(fooValue) {

  override fun equals(other: Any?): Boolean =
    (other is InterfaceDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

interface TwoFoos {
  val foo: Int
  val bar: String
}

data class TwoFoosImpl(override val foo: Int, override val bar: String): TwoFoos

class TwoFoosDelegation1(fooValue: Int, barValue: String): TwoFoos by TwoFoosImpl(fooValue, barValue) {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegation1) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegation2(baz: TwoFoos): TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegation2) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegation3(val baz: TwoFoos): TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegation2) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

/*
 * Lazy delegation
 */

class LazyDelegation(fooValue: Int) {
  val foo: Int by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

class LazyGenericDelegation(fooValue: List<Int>) {
  val foo: List<Int> by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyGenericDelegation) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class NestedLazyGenericDelegation(fooValue: List<List<List<Int>>>) {
  val foo: List<List<List<Int>>> by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is NestedLazyGenericDelegation) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class TwoLazyDelegations(fooValue: Int, barValue: String) {
  val foo: Int by lazy { fooValue }
  val bar: String by lazy { barValue }

  override fun equals(other: Any?): Boolean =
    (other is TwoLazyDelegations) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

/*
 * Other kinds of delegation
 */

val topLevelFoo: Int = 42

class TopLevelDelegation {
  val foo: Int by ::topLevelFoo

  override fun equals(other: Any?): Boolean =
    (other is TopLevelDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

class MemberDelegation(val fooValue: Int) {
  val foo: Int by this::fooValue

  override fun equals(other: Any?): Boolean =
    (other is MemberDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

data class AnotherClass(val bar: Int)
class AnotherClassDelegation(anotherClass: AnotherClass) {
  val foo: Int by anotherClass::bar

  override fun equals(other: Any?): Boolean =
    (other is AnotherClassDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

class MapDelegation(map: Map<String, Any>) {
  val foo: String by map

  override fun equals(other: Any?): Boolean =
    (other is MapDelegation) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class ReflectionDelegationProvider(val seed: Int) {
  operator fun getValue(thisRef: Any?, prop: KProperty<*>): String =
    "${prop.name}_${seed}"
}
class ReflectionDelegation(seed: Int) {
  val foo: String by ReflectionDelegationProvider(seed)

  override fun equals(other: Any?): Boolean =
    (other is ReflectionDelegation) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}
