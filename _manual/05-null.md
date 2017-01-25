---
title: Dealing with null
permalink: /manual/null/
---
`equals` methods are not allowed to throw exceptions. This includes NullPointerExceptions, and EqualsVerifier checks this. This has some consequences, though.

For safety, EqualsVerifier requires you to add null checks for non-primitive fields you reference in your `equals` or `hashCode` method. If you don't, you will get the following error message:

    Non-nullity: equals throws NullPointerException on field o.

Adding a null check is easy: instead of `x.equals(other.x)`, you write `Objects.equals(x, other.x)`.

However, you might not want to add a null check, for example because your class is immutable and you check for null in the constructor. There are several things you can do.

### Annotations
You can mark your fields with a `@Nonnull` annotation. EqualsVerifier recognizes annotations named `@Nonnull`, `@NonNull` and `@NotNull`. This should cover the annotations from all the popular annotations providers, like FindBugs and Eclipse.

In addition, EqualsVerifier supports FindBugs's deprecated `@DefaultAnnotation` and [JSR305's default annotations](http://stackoverflow.com/questions/11776302/how-to-indicate-that-member-fields-are-nonnull-by-default). In these two cases, EqualsVerifier also supports the `@CheckForNull` annotation to reverse the process for a single field.

If you already use these annotations for static analysis purposes, this is obviously the preferred way to deal with nulls in EqualsVerifier as well.

### Configuring EqualsVerifier
If annotations are not an option, you can configure EqualsVerifier to skip the null checks. There are two ways to do that. First, you can use `withNonnullFields` on individual fields:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .withNonnullFields("bar", "baz")
    .verify();
{% endhighlight %}

It accepts a varargs argument, so you can specify as many fields as you like. If you specify a field that doesn't exist on the class, EqualsVerifier throws an exception. This is done to avoid bugs caused by rename refactorings, since the fields have to be specified as strings. The (non-primitive) fields that you don't specify, still require a null check in the `equals` method.

The second way to skip the null checks, is simply to suppress the warning altogether:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .suppress(Warning.NULL_FIELDS)
    .verify();
{% endhighlight %}

This skips the null checks for all fields in the class.

