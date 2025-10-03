---
title: "Kotlin support"
permalink: /manual/kotlin/
---
EqualsVerifier has some support for Kotlin classes. In principle, Kotlin classes are the same as Java classes on the bytecode level, so EqualsVerifier can read them in the same way. However, the Kotlin compiler does some tricks in order to be able to represent its more advanced language features. This means that in some cases, EqualsVerifier needs to treat Kotlin classes a little differently. This is possible in many, but unfortunately not all, cases.

## Simple classes

Since EqualsVerifier works primarily with Java reflection, you'll have to provide EqualsVerifier with Java-style reflection types:

```kotlin
EqualsVerifier.forClass(Foo::class.java)
  .verify()
```

For most simple classes and data classes, that's enough.

## Delegates

Kotlin supports various forms of delegate fields. In a Kotlin class definition, delegates may look like this:

```kotlin
data class StringContainer(s: String)

class Foo(container: StringContainer) {
  private val bar: String by container::s   // object delegation
  private val baz: String by lazy { ... }   // lazy delegation
}
```

But EqualsVerifier works with Java reflection, which looks at the fields as they exist in the compiled bytecode. And at that level, these fields don't exist! When decompiled to Java, it looks like this:

```java
class Foo {
  private final StringContainer bar$receiver;
  private final kotlin.Lazy<String> baz$delegate;
}
```

EqualsVerifier can deal with most forms of Kotlin delegation, but in order to do so, it does require the `org.jetbrains.kotlin:kotlin-reflect` library to be available. You might have to add it to your `pom.xml` or your Gradle scripts. In some cases, EqualsVerifier can fall back to using bytecode names, but not in all cases. If EqualsVerifier needs it, it will let you know if it's not available.

EqualsVerifier can detect when a field is a Kotlin delegate, and it can translate between the Kotlin names and the bytecode names. So, in error messages, instead of `bar$receiver`, you will see `bar`. And instead of having to call `withIgnoredFields("baz$delegate")`, you can simply call `withIgnoredFields("baz")`. You can still call `withIgnoredFields("baz$delegate")` if you prefer, for example because you don't want to add `kotlin-reflect` to the project.

Unfortunately, when adding [prefab values](/equalsverifier/manual/prefab-values), you still have to provide values of the underlying type:

```kotlin
EqualsVerifier.forClass(Foo::class.java)
  .withPrefabValuesForField(Foo::bar.name, StringContainer("a"), StringContainer("b"))
  .withPrefabValuesForField(Foo::baz.name, lazy { "a" }, lazy { "b" })
  .verify()
```

Note also that some of the checks that EqualsVerifier normally does, like reflexivity checks, don't work for delegates. For example, the use of the triple-equals operator `===` will not be detected in this case:

```kotlin
class Foo(container: StringContainer) {
  private val foo: String by container::s

  override fun equals(other: Any?): Boolean =
    (other is Foo) && foo === other.foo
}
```

EqualsVerifier will generate two non-equal instances of `StringContainer`, but the Kotlin compiler generates bytecode that directly compares its `s` field in the `equals` method, without calling `equals` on `StringContainer` itself. Because of architectural decisions made before Kotlin even existed and which are hard to change now, EqualsVerifier gives both `StringContainers` the same String instance for their `s` fields, and because of that EqualsVerifier can't detect the difference between `==` or `===` (or in Java terms, the difference between `equals()` and `==`.)

## Interface delegation

A special case of delegation in Kotlin is interface delegation:

```kotlin
interface Foo {
  val foo: Int
}

data class FooImpl(override val foo: Int) : Foo

class InterfaceDelegation(fooValue: Int) : Foo by FooImpl(fooValue) {

  override fun equals(other: Any?): Boolean =
    (other is InterfaceDelegation) && foo == other.foo

  override fun hashCode(): Int = foo
}
```

In this case, the `FooImpl` instance will be stored in a field named `$$delegate_0`. Unfortunately, there is no way to reliably translate between this bytecode field and its Kotlin-level corresponding `foo` property. As a result, error messages will show these, more cryptic field names. Also, EqualsVerifier might ask more quickly for prefab values for the type that is delegated to (in this case: `FooImpl`).
