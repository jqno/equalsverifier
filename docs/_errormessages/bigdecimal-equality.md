---
title: "BigDecimal equality"
---
Be aware that `BigDecimal` fields with values such as `1`, `1.0`, `1.00`, ... are not considered equal.

The `Comparable` interface strongly recommends but does not require that implementations consider two objects equal using
`compareTo` whenever they are equal using `equals` and vice versa. `BigDecimal` is a class where this is not applied.

{% highlight java %}
BigDecimal one = new BigDecimal("1");
BigDecimal alsoOne = new BigDecimal("1.0");

// prints true - 1 is the same as 1.0
System.out.println(one.compareTo(alsoOne) == 0);
// prints false - 1 is not the same as 1.0
System.out.println(one.equals(alsoOne));
{% endhighlight %}

Ways to resolve this error
---
If values like `1` and `1.0` need not be considered equal then this check can be disabled by suppressing `Warning.BIGDECIMAL_EQUALITY`.

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .suppress(Warning.BIGDECIMAL_EQUALITY)
    .verify();
{% endhighlight %}

If values like `1` and `1.0` *should* be considered equal then some options are:
1. **Do not use `BigDecimal` as fields.** But unfortunately I cannot recommend well-known generally accepted drop-in alternative.

    The argument for this: it is not great having to complicate classes with the options below. A valid `equals`and
    `hashCode` is already easy enough to get wrong (option 2) and it is easy to forget and hard to validate `BigDecimal`
    fields have been normalised (option 3). More special handling in `equals` and `hashCode` moves against standardisation,
    such as making things easier e.g. `Objects.equals`, `Objects.hashCode`, or eliminating the need to think about it e.g.
    Lombok's `@EqualsAndHashCode` or Java 16 (preview since 14) `record` classes: it is a shame having to add boilerplate
    for every [`@EqualsAndHashCode` annotated class](https://stackoverflow.com/questions/36625347/how-to-make-lomboks-equalsandhashcode-work-with-bigdecimal)
    or every [`record` class](https://stackoverflow.com/questions/68690126/java-16-records-bigdecimal-equals-hashcode)
    that has a `BigDecimal` field.

2. **Implement `equals`  to use `compareTo` for `BigDecimal` fields** and ensure values of those fields that are equal using
    `compareTo` will produce the same hashcode.

    EqualsVerifier checks this by default which causes the *BigDecimal equality* error messages.

    It wouldn't be unwise to use utility methods as this is not as simple as a call to Java's `Objects.equals` and `Objects.hashcode`.
    The logic for correct equality is:

    {% highlight java %}
    // true if bdField and other.bdField are
    // either both null or are equal using compareTo
    boolean comparablyEqual = (bdField == null && other.bdField == null)
        || (bdField != null && other.bdField != null && bdField.compareTo(other.bdField) == 0);
    {% endhighlight %}

    A consistent hashcode needs a way to normalise the value that it represents. A simple normalisation is:

    {% highlight java %}
    // Remove trailing zeros from the unscaled value of the
    // BigDecimal to yield a consistently scaled instance
    int consistentHashcode = Objects.hashCode(bdField.stripTrailingZeros());
    {% endhighlight %}

3. **Normalise the `BigDecimal` fields** during your class's construction (and setters if it is mutable) such that there
    is only one way it represents the same value for these fields. This means standard `equals` and `hashCode` can be used.
    For example, your class ensures all variants of `1` such as `1.0`, `1.00`, etc are converted to `1` in its constructor.

    A simple normalisation is:

    {% highlight java %}
    class Foo {
        ...
        Foo(BigDecimal bdField) {
            // Now if bdField is final it will only have instances
            // that are equal when compareTo is equal.
            // If mutable then setters will need to do the same.
            this.bdField = bdField.stripTrailingZeros();
        }
    }
    {% endhighlight %}

    Unfortunately it is difficult to confirm this has been done. This check will then want disabling by suppressing `Warning.BIGDECIMAL_EQUALITY`
    and will not catch regressions.

If performance is important then you will need to consider the costs of using `BigDecimal` and of where and how normalisation
is achieved. Option 2 performs the work when objects are stored in a `HashSet` or used as keys in a `HashMap`. Option 3
performs work on creation of each object but is then cheaper to hash. There may be better normalisations than `stripTrailingZeros`.
`BigDecimal` already has a cost traded in return for its accuracy.

Why does this happen for BigDecimal?
---
`BigDecimal` can have multiple representations of the same value. It uses an *unscaled value* and a *scale* (both integers).
For example, the value of 1 can be represented as unscaled value 1 with scale of 0 (scale is the number of places after
the decimal point when 0 or greater) or as unscaled value 10 with scale of 1 resolving to 1.0. Its `equals` and `hashCode`
methods use both of these attributes in their calculation rather than the resolved value.

There is more information on `compareTo` and `equals` in the `Comparable` Javadoc and Effective Java's chapter on implementing `Comparable`.

There is more information on `BigDecimal` in its Javadoc (and its representation can be seen by printing `unscaledValue()` and `scale()`).
