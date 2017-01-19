---
title: Ignoring fields
---
By default, EqualsVerifier assumes that all (non-static) fields of your class participate in `equals` and `hashCode`.

This is great, because it ensures that if you add a field, you can't forget to update your `equals` and `hashCode` methods. If you do, EqualsVerifier will fail the test.

### Ignoring fields
Sometimes you don't want this, though. In that case, you can configure EqualsVerifier to ignore certain fields, like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .withIgnoredFields("bar", "baz")
    .verify();
{% endhighlight %}

It accepts a varargs argument, so you can specify as many fields as you like. If you specify a field that doesn't exist on the class, EqualsVerifier throws an exception. This is done to avoid bugs caused by rename refactorings, since the fields have to be specified as strings.

If you do this, EqualsVerifier assumes that the fields `bar` and `baz` don't participate in `equals`. If EqualsVerifier notices that they do despite this, EqualsVerifier will fail the test.


### Including fields
If your class has a lot of fields, but it determines equality based on only a few of them, you can also turn it around and specify precisely the fields you want:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .withOnlyTheseFields("bar", "baz")
    .verify();
{% endhighlight %}

Now only `bar` and `baz` can participate in `equals`. EqualsVerifier fails the test if any other field participates in `equals`, or if `bar` or `baz` somehow don't participate. It will also fail the test if no field called `bar` or `baz` exists in the class.

Like `withIgnoredFields`, `withOnlyTheseFields` accepts a varargs argument, so you can specify as few or as many fields as you need.

