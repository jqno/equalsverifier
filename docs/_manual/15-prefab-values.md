---
title: "What are these prefab values?"
permalink: /manual/prefab-values/
---
Sometimes, EqualsVerifier asks to provide "prefab values". What are they, and why does EqualsVerifier need them?

Let's say you're testing a class `Book` that looks like this:

```java
class Book {
    private final String title;
    private final Person author;

    // Constructor, equals(), hashCode() omitted
}

EqualsVerifier.forClass(Book.class)
    .verify();
```

If EqualsVerifier is going to test this class, it needs to create two instances of it, so it can call `equals()`:

```java
var red = new Book(...);
var blue = new Book(...);

if (red.equals(blue)) {
    // is that what we expected?
}
```

Unfortunately, EqualsVerifier can't just call the constructor like that, because it doesn't know if that will properly initialize all the fields, and EqualsVerifier needs to very carefully control the values of the fields in order to be able to verify whether `equals()` works like it's supposed to.

So what EqualsVerifier does, is this: first, it instantiates a `Book` without calling the constructor. (It uses [Objenesis](https://objenesis.org) for that, a specialized library whose sole purpose it is to instantiate objects without calling the constructor. It uses all kinds of dark magic to do that.) Then, it uses reflection to assign values to the `title` and `author` fields. It does this several times, for different values of `title` and `author`. Then it calls `equals()` for each combination of values and checks whether the outcome is as expected.

(Note that for records, there is a strong guarantee that a constructor exists that has parameters that map 1-on-1 to its fields. So, for records, EqualsVerifier can use that constructor and doesn't need Objenesis. But it still needs to create instances of the fields in the same way.)

For the `title` field, this is easy. EqualsVerifier knows about `String` and contains a list of values it can use. EqualsVerifier knows about most of the built-in types, but `Person` is harder. It has to instatiate that too, and fill in all its fields:

```java
class Person {
    private final String name;
    private final int yearOfBirth;

    // Constructor, equals(), hashCode() omitted
}
```

This is no problem, because EqualsVerifier knows about `String` and `int`, so it can construct a `Person` with values for `name` and `yearOfBirth` without issue.

However, what if the `Person` class looked like this:

```java
class Person {
    private final String name;
    private final int yearOfBirth;
    private final Person marriedTo;

    // Constructor, equals(), hashCode() omitted
}
```

The `marriedTo` field has type `Person`, so now the type is cyclic (or recursive): in order to instantiate a `Person`, we need another `Person`. It could also assign `null`, but it also needs to test the situations where `marriedTo` is _not_ `null`. This is a situation EqualsVerifier can't resolve by itself, and it will ask for a prefab value, which is short for "pre-fabricated value", which means, a value that was pre-fabricated by you, the user of EqualsVerifier. This is how you can do that:

```java
var redPrefab = new Person(...);
var bluePrefab = new Person(...);

EqualsVerifier.forClass(Book.class)
    .withPrefabValues(Person.class, redPrefab, bluePrefab)
    .verify();
```

There are some other situations where EqualsVerifier can't create values by itself. Most importantly, when a field is from a module that's not open for reflection. Since most people run EqualsVerifier on the classpath, this is usually a module from the JDK itself, like `java.base` or `java.sql`. You can solve that in the same way, by providing a prefab value. However, since these classes are bundled with the JDK, EqualsVerifier maintains an internal list of these classes with some pre-fabricated values. These are referred to as built-in prefab values, and if you think EqualsVerifier is missing one, you can [report it](https://github.com/jqno/equalsverifier/issues/new).

If you run EqualsVerifier on the modulepath, you might run into this problem more often, and you'll either have to provide prefab values, or open the module up for reflection as discussed in [the chapter on JPMS](/equalsverifier/manual/jpms).

## Mockito

Starting with version 4.0, EqualsVerifier can detect if Mockito is available on the classpath or modulepath. If so, it will first attempt to use Mockito to mock a field, before asking for a prefab value. This way, it becomes unnecessary in many cases to add prefab values, which makes it a lot easier to use EqualsVerifier:

```java
EqualsVerifier.forClass(Book.class)
    .verify();
```

EqualsVerifier now uses Mockito to mock instances for `Person` that it can use to do the checks.

If, for whatever reason, you don't want to use Mockito in this way, you can instruct EqualsVerifier to skip it:

```java
EqualsVerifier.forClass(Book.class)
    .set(Mode.skipMockito())
    .withPrefabValues(Person.class, ...)
    .verify();
```

There is one caveat to this. EqualsVerifier relies on the fact that this relation holds:

```java
var red = mock(Person.class);
var blue = mock(Person.class);
assertFalse(red.equals(blue));
```

In other words, it expects that two different mocks of the same type are never equal to each other. Similarly, it expects that two mocks never have the same hashCode. In practice, this is always the case. However, this behavior of Mockito is not actually _documented_. Rahter, it's an implementation detail for Mockito, and as such, Mockito is free to change this in a future version. If they do, this will no longer work, and EqualsVerifier will have to drop this feature. Caveat emptor.

## forExamples

If you need to provide many prefab values for a single class, it might be easier to use the `forExamples` method, which was also introduced in EqualsVerifier 4.0.

It works like this:

```java
var red = new Book("Don Quixote", new Person("Cervantes", 1547));
var blue = new Book("Hitch-Hiker's Guide to the Galaxy", new Person("Douglas Adams", 1952));
// Let's assume both authors were married and their `marriedTo` field is correctly initialized

EqualsVerifier.forExamples(red, blue)
    .verify();
```

In this situation, EqualsVerifier expects `red` and `blue` to have exactly the same class, and it expects that their fields are all different. In other words, `red.name` cannot be equal to `blue.name`; `red.yearOfBirth` cannot be equal to `blue.yearOfBirth`, et cetera.

Since EqualsVerifier can now take valid values from these two instances `red` and `blue`, it will not have to ask for prefab values.

## Special case: internal invariants

If your class has an internal invariant, i.e. some condition that must always be true, this might cause a problem for EqualsVerifier. For example, if you have a record like this:

```java
record Adult(String name, int age) {
    public Adult {
        if (age < 18) {
            throw new IllegalArgumentException("Age must be 18 or higher");
        }
    }
}
```

EqualsVerifier will fail, because its built-in prefab values for `int` are `1` and `2`. In this case, you can do a couple of things.

First, you can provide a prefab value of your own for `int`, like described above:

```java
EqualsVerifier.forClass(Adult.class)
    .withPrefabValues(int.class, 21, 99)
    .verify();
```

However, if you do that, these values will apply to _all_ `int`s in the class. This might be OK, but it might not be. For example, if you have a `numberOfSiblings` field of type `int`, then `99` might not be a plausible value that you want EqualsVerifier to use.

In such a situation, you can also use `withPrefabValuesForField`:

```java
EqualsVerifier.forClass(Adult.class)
    .withPrefabValuesForField("age", 21, 99)
    // No need to provied prefab values for `numberOfSiblings`,
    // because the defaults are fine
    .verify();
```

The given values will now _only_ be used for the specified field. Of course, EqualsVerifier will check first if the field exists, and if the value types match the type of the field.

Finally, you can use `forExamples()`, as described in the previous section, because using that behaves exactly the same as if you called `withPrefabValuesForField()` for each field in the type.

## Special case: generics

Sometimes, you need a prefab value for a generic type. It might be possible to just use `withPrefabValues()` or `withPrefabValuesForField()`, but sometimes you need more control. In those situations, you could use `withGenericPrefabValues()`. For example, if you want to test this class:

```java
class Foo {
    private final Bar<String> stringBar;
    private final Bar<Integer> intBar;

    // Constructor, equals(), hashCode() omitted
}
```

You can test it like this:

```java
EqualsVerifier.forClass(Foo.class)
    .withGenericPrefabValues(Bar.class, t -> new Bar<>(t));
```

That way, you can precisely control how you want the generic type to be constructed, and you don't have to add separate variants for each field using `withPrefabValuesForField()`.
