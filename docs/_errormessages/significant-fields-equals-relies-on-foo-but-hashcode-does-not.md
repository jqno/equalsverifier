---
title: "Significant fields: equals relies on foo, but hashCode does not"
---
The cause for this message is usually the obvious one, and is easily fixed either by adding it to `hashCode()` or removing it from `equals()`.

If that doesn't help, or if that's not desired, you can tell EqualsVerifier to ignore a field by [calling `withIgnoredFields`](/equalsverifier/manual/ignoring-fields).

One special case is when EqualsVerifier generates instances for the class's fields that happen to have the same hashCodes. For instance, the `Interval` class from [ThreeTen-extra](https://www.threeten.org/threeten-extra/apidocs/org.threeten.extra/org/threeten/extra/Interval.html) implements `hashCode()` like this: 

{% highlight java %}
@Override
public int hashCode() {
    return start.hashCode() ^ end.hashCode();
}
{% endhighlight %}

EqualsVerifier selects the same values for `start` and `end`, causing the `hashCode()` method to return equal values for both instances.

In such a case, the best way to solve this is by using `withPrefabValues` to provide more suitable instances for this type.

Another special case is when one of the fields is a cached hashCode field, similar to the one in `java.lang.String`. For example, given the following (abridged) class:

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

Another scenario in which you might experience this error message is when using Lombok's `@EqualsAndHashCode` with `cacheStrategy=LAZY`: 

{% highlight java %}
@RequiredArgsConstructor
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class CachedHashCode {
  private final String foo;
}
{% endhighlight %}

Using `.withLombokCachedHashCode` allows to test those classes as well: 

{% highlight java %}
EqualsVerifier.forClass(LazyPojo.class)
    .withLombokCachedHashCode(new CachedHashCode("bar"));
{% endhighlight %}

For more help on how to use `withCachedHashCode`, read the [manual page about it](/equalsverifier/manual/caching-hashcodes).
