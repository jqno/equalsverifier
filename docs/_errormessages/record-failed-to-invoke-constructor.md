---
title: Record: failed to invoke constructor
---
    Record: failed to invoke constructor
    If the record does not accept 0 as a value for its fields, consider providing valid prefab values for those fields and suppressing Warning.ZERO_FIELDS.

Sometimes, a constructor needs to validate its input, throwing an exception on certain values. This can lead to problems when the value `0` is not allowed, and the constructor is part of a record instead of a regular class, like this:

{% highlight java %}
public record Foo(int i) {
    public Foo {
        if (i < 42) {
            throw new IllegalArgumentException();
        }
    }
}
{% endhighlight %}

One of the things EqualsVerifier does, is to run checks with fields set to their default values. For reference types, the default value is `null`, which has its own [chapter in the manual](/equalsverifier/manual/null). For primitive types, the default value is `0` (or `0.0`, `\u0000`, `false`). In regular classes, EqualsVerifier bypasses the constructor, so the exception can never be thrown. However, reflection support is much more limited for records, and their constructors cannot be bypassed.

Therefore, we need to signal EqualsVerifier to skip the checks with default values, by suppressing `Warning.ZERO_FIELDS`.

Another thing EqualsVerifier does, is to run checks with fields set to certain prefab values. For integral types, these values are `1` and `2`. If these values are not allowed by the record's constructor either, we need to provide new prefab values as well.

In these cases, the call to EqualsVerifier will look like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
        .suppress(Warning.ZERO_FIELDS)
        .withPrefabValues(int.class, 42, 1337)
        .verify();
{% endhighlight %}
