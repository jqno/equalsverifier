---
title: "`instanceof` or `getClass()`"
permalink: /manual/instanceof-or-getclass/
---
An `equals` method needs to check the type of its argument, and there's two ways to do that: either with an `instanceof` check, or with a `getClass()` check:

{% highlight java %}
public boolean equals(Object obj) {
    if (!(obj instanceof Foo)) return false;
    // ...
}

public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) return false;
    // ...
}
{% endhighlight %}

(Note, with `instanceof` you don't need an explicit null check, because `instanceof` already does that for you.)

Both are valid, but unfortunately, EqualsVerifier can't always tell the difference.

Josh Bloch, in his Effective Java, recommends using `instanceof` over `getClass()` because `instanceof` plays nicer with the [Liskov substitution principle](https://en.wikipedia.org/wiki/Liskov_substitution_principle). EqualsVerifier follows this advice.

You might ask: why is this Liskov substitution principle important? Well, tools like Hibernate, and many mocking frameworks such as Mockito and EasyMock, use bytecode manipulation tricks to generate subclasses on the fly. That means that an object that you instantiate yourself could never be equal to an object that you fetch from the database, even if all the fields have the same values, simply because they're not of the same type.

However, many IDEs generate `equals` methods with `getClass()` checks by default. Or maybe you prefer `getClass()` over `instanceof`. In those cases, you may need to call `usingGetClass()` on EqualsVerifier, like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .usingGetClass()
    .verify();
{% endhighlight %}

If you use `getClass()` and you forget to add `usingGetClass()`, you might get this error message:

> Subclass: object is not equal to an instance of a trivial subclass with equal fields:<br>
> &nbsp;&nbsp;nl.jqno.equalsverifier.Foo@123456<br>
> Maybe you forgot to add usingGetClass(). Otherwise, consider making the class final.

This is an example where the Liskov substitution principle and `getClass()` clash. EqualsVerifier creates a subclass of `Foo`, but adds nothing to it. Then it expects that `equals` treats it the same as an actual `Foo`. It doesn't because `getClass()` returns different values for `Foo` and for the subclass.

Making `Foo` final is another way to avoid this problem.

If you want to know more about this, read Effective Java's chapter on `equals`.

