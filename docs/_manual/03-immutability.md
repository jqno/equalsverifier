---
title: Immutability
permalink: /manual/immutability/
---
EqualsVerifier often asks you to make your fields final:

    Mutability: equals depends on mutable field foo.

This is because of `equals`'s consistency requirement: if you call `equals` on the same objects twice, you expect the same results twice. If you change the values of the fields in between, the result of calling `equals` will change, too.

That's important when you use your class as a key in a `HashMap`, or when you add it to a `HashSet`. Suddenly, you may not be able to find your objects anymore, even though they're there.

The best way to avoid this problem is by making your class immutable. For that, all fields need to be final, and EqualsVerifier checks that. It's also necessary that the types of your fields are themselves immutable. Unfortunately, this is something that EqualsVerifier can't verify.

If you can't make the fields final, but you're absolutely certain that the class is immutable, you can add an `@Immutable` annotation to the class. (Note that a class with only final fields may not be immutable: generally, its fields must all be immutable too, and the class must probably be final as well.) As far as EqualsVerifier is concerned, it doesn't matter where the `@Immutable` annotation comes from; you can even write one yourself, as long as it's called `Immutable`. However, since several sources offer such an annotation, it is wise to carefully pick the most appropriate one.

If you don't care about immutability, as a last resort you can add a `suppress(Warning.NONFINAL_FIELDS)` to your call to EqualsVerifier:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .suppress(Warning.NONFINAL_FIELDS)
    .verify();
{% endhighlight %}

