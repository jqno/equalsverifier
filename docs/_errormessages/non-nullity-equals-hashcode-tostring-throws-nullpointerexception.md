---
title: "Non-nullity: equals/hashCode/toString throws NullPointerException"
---
This error occurs when the class under test can throw a `NullPointerException` when one of its fields is null and `equals`/`hashCode`/`toString` is called. For example, `equals` could contain this line:
{% highlight java %}
return foo.equals(other.foo);
{% endhighlight %}
It will throw a `NullPointerException` if `foo` is null. This can be avoided in multiple ways:

* In Java 7 and up, you can use `Objects.equals`, which is null-safe:
{% highlight java %}
return Objects.equals(foo, other.foo);
{% endhighlight %}

* You can add a null check, like so:
{% highlight java %}
return foo == null ? other.foo == null : foo.equals(other.foo);
{% endhighlight %}

* If you're certain the field can never be null (for instance, because the class's constructor explicitly checks for it), you can add `.withNonnullFields("foo")` to your call to EqualsVerifier.

* If you use @NonNull/@NotNull/@Nullable or similar annotations in your code, then make sure the annotations are available on the classpath when the test is executed.

* If this problem occurs for many fields in the same class, you can suppress `Warning.NULL_FIELDS` in your call to EqualsVerifier.

You can read more about dealing with null in the [manual page about null](/equalsverifier/manual/null).

