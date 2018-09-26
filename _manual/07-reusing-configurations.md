---
title: Re-using configurations
permalink: /manual/reusing-configurations/
---
When you have a large domain with many classes that all have `equals` methods to be tested, using EqualsVerifier the way we have been until now can get a bit unwieldy, especially if each call needs to be configured in the same way.

For instance, if your team has decided to use `getClass`-based equality checks instead of `instanceof`-based checks, there will be a lot of repetition in your tests:

{% highlight java %}
EqualsVerifier.forClass(Student.class)
    .usingGetClass()
    .verify();

EqualsVerifier.forClass(Teacher.class)
    .usingGetClass()
    .verify();

EqualsVerifier.forClass(Staff.class)
    .usingGetClass()
    .verify();
{% endhighlight %}

In such cases, you can use a configuration object to reduce the boilerplate:

{% highlight java %}
EqualsVerifierApi ev = EqualsVerifier.configure().usingGetClass();

ev.forClass(Student.class)
    .verify();
ev.forClass(Teacher.class)
    .verify();
ev.forClass(Staff.class)
    .verify();
{% endhighlight %}

The `ev` object can now be reused freely.

This works for `.usingGetClass()`, `.suppress(...)`, `.withPrefabValues(...)` and `.withGenericPrefabValues()`, since these are not specific to the class under test.

Of course, you can still further configure each call:

{% highlight java %}
ev.forClass(Manager.class)
    .suppress(Warning.NULL_FIELDS)
    .verify();
{% endhighlight %}

