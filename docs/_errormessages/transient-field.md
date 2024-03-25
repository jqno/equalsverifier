---
title: Transient field
---
    Transient field foo should not be included in equals/hashCode contract

This error message shows up if you include a transient field in `equals` or `hashCode`. The purpose of transient fields is that [they aren't part of the persisted state of an object](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.3.1.3). Therefore, they shouldn't be part of `equals` either.

This applies to fields marked with Java's `transient` keyword, and fields annotated with the JPA `@Transient` annotations.

If, for whatever reason, you _do_ need to include a transient field in `equals` or `hashCode`, you can suppress `Warning.TRANSIENT_FIELDS` like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
        .suppress(Warning.TRANSIENT_FIELDS)
        .verify();
{% endhighlight %}
