---
title: "Final means final"
permalink: /manual/final-means-final/
---
EqualsVerifier needs instantiated objects to do its work, but the way you indicate what class you want to test is by passing EqualsVerifier a class object. EqualsVerifier needs a way to turn this class into an instantiated object, and traditionally, it did that by leveraging [Objenesis](https://objenesis.org/) to create the object, and then using reflection to set the values of the fields.

This changed slightly in Java 16 with the introduction of records. Records don't allow reflection to change their fields. Fortunately, records are guaranteed to have a 1-to-1 mapping between their fields and their constructor parameters, so EqualsVerifier can call a record's constructor to instantiate it.

Starting with Java 26 and [JEP 500: "Prepare to Make Final Mean Final"](https://openjdk.org/jeps/500) though, Java is on a path to make it impossible for normal classes as well. In Java 26, a warning is emitted when using reflection to change a final field. In later versions, an exception will be thrown.

Therefore, starting with Java 26, EqualsVerifier has to adapt its way to instantiate objects, since reflection is no longer guaranteed to work. EqualsVerifier will now pick one of a number of strategies to instantiate objects:

- If it's a record, call the constructor.
- If a class has a constructor that covers all the fields in the same order, it will also call that constructor.
- If a class has no final fields, such as a JPA entity, it will continue to use reflection, since reflection still works on non-final fields.
- Perhaps more strategies will be added in the future.

However, what to do when none of these strategies will work? For those cases, EqualsVerifier 4.4 has introduced the `#withFactory()` method. It works like this:

```java
class Foo {
    private final int i;
    private final String s;
    private final Bar b;

    public Foo(int i, String s, Bar b, Object hereToMakeConstructorStrategyFail) {
        this.i = i;
        this.s = s;
        this.b = b;
    }
}

EqualsVerifier.forClass(Foo.class)
    .withFactory(values -> new Foo(
        values.getInt("i"),
        values.getString("s"),
        values.get("b"),
        new Object()))
    .verify();
```

The `#withFactory()` method accepts a functional interface that takes a `Values` object and returns an instance of the class under test. `Values` is a wrapper around `Map`, and it contains a value for each of the fields, index by the name of that field. It has dedicated methods for all primitive types, like `#getInt()` and `#getLong()`, it has `#getString()` for Strings, and it has `#get()` for all other objects. This should allow you to construct the object, by calling some constructor, or by calling a factory method, or maybe something else.

If EqualsVerifier needs a factory but doesn't get one, it will give an error message that makes it clear what to do:

```plaintext
-> Cannot instantiate NonConstructableParent.
   Use #withFactory() so EqualsVerifier can construct NonConstructableParent instances without using reflection.
```

## Preparing to migrate

If you plan on migrating to a JDK where "final means final" but are unsure if you'll get a lot of warnings from EqualsVerifier, you can force 'final means final' mode:

```java
EqualsVerifier.forClass(Foo.class)
    .set(Mode.finalMeansFinal())
    .verify();
```

In this case, EqualsVerifier will not use reflection on final fields, even if it could. Instead, it will use one of the strategies mentioned above, and if they fail, it will ask for a factory.

## Inheritance

Unless `Foo` is final, EqualsVerifier also wants to run some tests on a subclass of `Foo`. Traditionally, it does that by generating a subclass using [Byte Buddy](https://bytebuddy.net/), a bytecode manipulation library. However, if EqualsVerifier needs a factory to instantiate `Foo`, it won't be able to generate a subclass that it can instantiate without the help of a factory, so in that case, you will need to provide a subclass and a factory as well:

```java
class Foo {
    private final int i;

    public Foo(int i, Object requireFactory) {
        this.i = i;
    }
}

EqualsVerifier.forClass(Foo.class)
    .withFactory(
        values -> new Foo(values.getInt("i"), new Object())
        values -> new Foo(values.getInt("i"), new Object()) {}
    )
    .verify();
```

(Note the `{}` at the end of the second factory, which instantiates a subclass.)

In more complex inheritance patterns, where state is added to subclasses and `#withRedefinedSuperclass()` and `#withRedefinedSubclass()` are needed (see [the chapter about inheritance](/equalsverifier/manual/inheritance)), these two methods gain an overload for providing factories:

```java
EqualsVerifier.forClass(Foo.class)
    .withFactory(
        values -> new Foo(values.getInt("i"), new Object())
        values -> new Foo(values.getInt("i"), new Object()) {}
    )
    .withRedefinedSubclass(
        values -> new SubFoo(values.getInt("i"), values.getString("s"), new Object())
    )
    .verify();

EqualsVerifier.forClass(SubFoo.class)
    .withFactory(
        values -> new SubFoo(values.getInt("i"), values.getString("s"), new Object())
        values -> new SubFoo(values.getInt("i"), values.getString("s"), new Object()) {}
    )
    .withRedefinedSuperclass(
        values -> new Foo(values.getInt("i"), new Object())
    )
    .verify();
```

Again, if EqualsVerifier needs such a factory but doesn't get one, it will make it clear with an error message:

```plaintext
-> Cannot instantiate a subclass of NonConstructableSuper (attempted subclass: NonConstructableSubForNonConstructableSuper).
   Use the overload of #withRedefinedSubclass() to specify a subclass.
```
