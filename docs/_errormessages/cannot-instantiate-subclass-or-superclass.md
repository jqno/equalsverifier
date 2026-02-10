---
title: "Cannot instantiate subclass/superclass of Foo. Use the overload ..."
---
```plaintext
Cannot instantiate a subclass of Foo (attempted subclass: Bar).
Use the overload of #withRedefinedSubclass() to specify a subclass.
```

```plaintext
Cannot instantiate the superclass of Bar (attempted superclass: Foo
Use the overload of #withRedefinedSuperclass() to specify a superclass.
```

EqualsVerifier needs instantiated objects to work and uses reflection to get them. This also applies to subclasses and superclasses when using the `#withRedefinedSubclass()` and `#withRedefinedSuperclass()` methods.

In Java 26 and above, [JEP 500](https://openjdk.org/jeps/500) makes it so that warnings will be issued when such reflection happens, and in later versions, an error will be thrown.

In situations when an error would be thrown, EqualsVerifier produces the above error message instead. You can solve it by using the overloads of `#withRedefinedSubclass()` and `#withRedefinedSuperclass()`, like this:

```java
EqualsVerifier.forClass(Foo.class)
    .withFactory(values -> new Foo(values.get("bar")))
    .withRedefinedSubclass(values -> new Baz(values.get("bar"), values.get("quux")))
    .verify();

EqualsVerifier.forClass(Baz.class)
    .withFactory(values -> new Baz(values.get("bar"), values.get("quux")))
    .withRedefinedSuperclass(values -> new Foo(values.get("bar")))
    .verify();
```

For more details about this, read [the chapter in the manual about "final means final"](/equalsverifier/manual/final-means-final).
