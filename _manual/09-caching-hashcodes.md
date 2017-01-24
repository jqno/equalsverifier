---
title: Caching hashCodes
permalink: /manual/caching-hashcodes/
---
Normally, a hashCode is calculated every time you call the `hashCode()` method. That's usually not a problem, since the calculation is usually not very complex.

Sometimes, however, a hashCode needs to be cached for performance reasons. Java's own `String` class is a good example where the hashCode is only calculated once, and then cached.

Since EqualsVerifier relies on reflection to change the content of fields and then calling `equals` and `hashCode` to see if they changed, it can't deal with cached hashCodes very well.

You can instruct EqualsVerifier to work with cached hashCodes, but it takes some work, and it only works with immutable classes. There are three things you have to do.

1. First, your class must contain a `private int` field that contains the cached hashCode.

1. Second, the class must have a method that can initialize and/or update the hashCode. This method can have no parameters, it cannot be public, and it must return the hashCode as an `int`. That means that the method can't assign to the field that contains the cached hashCode. The assignment has to happen in the constructor.

1. Finally, you must give EqualsVerifier an example of an object with a correctly initialized hashCode. EqualsVerifier uses this to make sure that your class isn't cheating, and that the method from the second point is actually used to assign to the field from the first point.

These three elements must be passed to EqualsVerifier's `withCachedHashCode` method.

All of this is pretty cumbersome, but it's necessary for technical reasons. You can correctly implement cached hashCodes in different ways, but EqualsVerifier can only test them if they're implemented in this particular way.

Here is an example of a class which implements a cached hashCode in a way that EqualsVerifier can deal with:

{% highlight java %}
class ObjectWithCachedHashCode {
    private final String name;
    private final int cachedHashCode;

    public ObjectWithCachedHashCode(String name) {
        this.name = name;
        this.cachedHashCode = calcHashCode();
    }

    @Override
    public final int hashCode() {
        return cachedHashCode;
    }

    private int calcHashCode() {
        return name.hashCode();
    }

    // equals method elided for brevity
}
{% endhighlight %}

And here is an example of an EqualsVerifier test that exercises this cached hashCode:

{% highlight java %}
@Test
public void testCachedHashCode() {
    EqualsVerifier.forClass(ObjectWithCachedHashCode.class)
            .withCachedHashCode("cachedHashCode", "calcHashCode",
                                new ObjectWithCachedHashCode("something"))
            .verify();
}
{% endhighlight %}

Note that EqualsVerifier will fail the test if `cachedHashCode` or `calcHashCode` can't be found in the class.


### When a class is hard to construct
Sometimes it's hard to construct a one-off instance of a class to serve as an example (see the third point, above). The class may have a lot of dependencies, or might be part of an elaborate inhertiance hierarchy. If this is the case, and you have other ways to test that the hashCode is indeed initialized when the object is constructed, there is a way to bypass the example:

{% highlight java %}
EqualsVerifier.forClass(ObjectWithCachedHashCode.class)
        .withCachedHashCode("cachedHashCode", "calcHashCode", null)
        .suppress(Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE)
        .verify();
{% endhighlight %}

The code for this (suppressing a warning with a very long name, passing a `null` value) is intentionally left a bit ugly, to urge you to do this only when it's absolutely necessary ;).

