---
title: "Cannot instantiate Foo. Use #withFactory()"
---
```plaintext
Cannot instantiate Foo.
Use #withFactory() so EqualsVerifier can construct Foo instances without using reflection.
```

EqualsVerifier needs instantiated objects to work and uses reflection to get them. In Java 26 and above, [JEP 500](https://openjdk.org/jeps/500) makes it so that warnings will be issued when that happens, and in later versions, an error will be thrown.

In situations when an error would be thrown, EqualsVerifier produces the above error message instead. You can solve it by using the `#withFactory()`, like this:

```java
EqualsVerifier.forClass(Foo.class)
    .withFactory(values -> new Foo(values.get("bar")))
    .verify();
```

For more details about this, read [the chapter in the manual about "final means final"](/equalsverifier/manual/final-means-final).
