package nl.jqno.equalsverifier.kotlin.delegates

import kotlin.reflect.KProperty

/*
 * No delegation
 */

class Normal(val foo: Int) {

  override fun equals(other: Any?): Boolean =
    (other is Normal) && foo == other.foo

  override fun hashCode(): Int = foo
}

/*
 * Interface delegation
 */

interface Foo {
  val foo: Int
}

data class FooImpl(override val foo: Int) : Foo

class InterfaceDelegation(fooValue: Int) : Foo by FooImpl(fooValue) {

  override fun equals(other: Any?): Boolean =
    (other is InterfaceDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}

class IncorrectInterfaceDelegation(fooValue: Int) : Foo by FooImpl(fooValue) {

  override fun equals(other: Any?): Boolean =
    (other is IncorrectInterfaceDelegation) && foo == other.foo

  override fun hashCode(): Int = 42
}

interface TwoFoos {
  val foo: Int
  val bar: String
}

data class TwoFoosImpl(override val foo: Int, override val bar: String) : TwoFoos

class TwoFoosDelegationWithExplicitFields(fooValue: Int, barValue: String) :
  TwoFoos by TwoFoosImpl(fooValue, barValue) {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegationWithExplicitFields) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegationWithParamToClass(baz: TwoFoosImpl) : TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegationWithParamToClass) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegationWithFieldToClass(val baz: TwoFoosImpl) : TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegationWithFieldToClass) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegationWithParamToInterface(baz: TwoFoos) : TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegationWithParamToInterface) && foo == other.foo && bar == other.bar

  override fun hashCode(): Int = (31 * foo) + bar.hashCode()
}

class TwoFoosDelegationWithFieldToInterface(val baz: TwoFoos) : TwoFoos by baz {
  override fun equals(other: Any?): Boolean =
    (other is TwoFoosDelegationWithFieldToInterface) && foo == other.foo && bar == other.bar

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

class LazyDelegationWithGenericValue(fooValue: List<Int>) {
  val foo: List<Int> by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyDelegationWithGenericValue) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class LazyDelegationWithNestedGenericValue(fooValue: List<List<List<Int>>>) {
  val foo: List<List<List<Int>>> by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyDelegationWithNestedGenericValue) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class LazyDelegationWithGenericClass<T>(fooValue: T) {
  val foo: T by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyDelegationWithGenericClass<*>) && foo == other.foo

  override fun hashCode(): Int = foo.hashCode()
}

class LazyDelegationWithBoundedGenericClass<T : Comparable<T>>(fooValue: T) {
  val foo: T by lazy { fooValue }

  override fun equals(other: Any?): Boolean =
    (other is LazyDelegationWithBoundedGenericClass<*>) && foo == other.foo

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

const val topLevelFoo: Int = 42

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

data class DoubleOtherClass(val foo: Int, val bar: String)
class DoubleOtherClassDelegation(doubleOtherClass: DoubleOtherClass) {
  val baz: Int by doubleOtherClass::foo
  val quux: String by doubleOtherClass::bar

  override fun equals(other: Any?): Boolean =
    (other is DoubleOtherClassDelegation) && baz == other.baz && quux == other.quux

  override fun hashCode(): Int = (31 * baz) + quux.hashCode()
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

