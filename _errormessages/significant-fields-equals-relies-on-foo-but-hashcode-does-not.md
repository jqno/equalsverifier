---
title: "Significant fields: equals relies on foo, but hashCode does not"
---
The cause for this message is usually the obvious one.

If it's not, then it might be caused by a cached hashCode field, similar to the one in `java.lang.String`. For example, given the following (abridged) class:

{% highlight java %}
class CachedHashCode {
    private final int foo;
    private final int bar;
    private int cachedHashCode = 0;

    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = calculateHashCode();
        }
        return cachedHashCode;
    }

    private int calculateHashCode() {
        int result = 0;
        result += 31 * foo;
        result += 31 * bar;
        return result;
    }
}
{% endhighlight %}

EqualsVerifier thinks `cachedHashCode` is a regular field like all the others, and assigns it a value other than 0. The first thing the `hashCode` method does is compare it to 0. This yields false, so the method always returns EqualsVerifier's (incorrect) value. EqualsVerifier doesn't know about this, so when it tries changing `foo`, it notices a difference in the result of `equals`, but not in the result of `hashCode` (since the latter is never properly initialized). And this is why EqualsVerifier thinks the problem is caused by `foo`.

This can be circumvented by using `withCachedHashCode`. You can call it like this:

{% highlight java %}
EqualsVerifier.forClass(CachedHashCode.class)
    .withCachedHashCode("cachedHashCode", "calculateHashCode", new CachedHashCode());
{% endhighlight %}

In order to be able to fully test that the hashCode is initialized correctly, it is necessary to put the actual hashCode calculation in a separate method, as seen above, and also to provide a manually constructed object that serves as an example.

