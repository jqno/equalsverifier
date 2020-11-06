---
title: Testing several classes at once
permalink: /manual/several-classes-at-once/
---
When you have a large domain with many classes that all have `equals` methods to be tested, using EqualsVerifier the way we have been until now can get a bit unwieldy, because you have to repeat the call to EqualsVerifier for each class that you want to test.

If all classes to test live within the same package, you can simplify quite a bit:

{% highlight java %}
EqualsVerifier.forPackage("com.example.app.domain")
    .verify();
{% endhighlight %}

This will test each class within the `com.example.app.domain` package.

If there's a class within that package that you don't want to test with EqualsVerifier, for instance a helper class, you can exclude it as follows:

{% highlight java %}
EqualsVerifier.forPackage("com.example.app.domain")
    .except(Helper.class)
    .verify();
{% endhighlight %}

You can achieve even more granularity:

{% highlight java %}
EqualsVerifier.forClasses(Student.class, Teacher.class, Staff.class, Address.class)
    .verify();
{% endhighlight %}

Both `.forPackage(...)` and `.forClasses(...)` can be configured with `.usingGetClass()`, `.suppress(...)`, `.withPrefabValues(...)` and `.withGenericPrefabValues(...)`, since these are not specific to a single class.

However, sometimes you need to configure even further, for instance when you need to use `withIgnoredFields`, `withNonnullFields` or `withRedefinedSubclass`. In such cases, you can use a configuration object to reduce the boilerplate:

{% highlight java %}
ConfiguredEqualsVerifier ev = EqualsVerifier.configure().usingGetClass();

ev.forClass(Student.class)
    .withIgnoredFields("grade")
    .verify();
ev.forClass(Teacher.class)
    .withIgnoredFields("salary")
    .verify();
ev.forClass(Staff.class)
    .withRedefinedSubclass(Janitor.class)
    .verify();
{% endhighlight %}

All of the EqualsVerifier tests in the snippet above will include the `.usingGetClass()` configuration defined on the `ev` configuration object.

Note that using `.forPackage(...)` can be slow, because all the files in all the JAR files on the classpath need to be scanned. If you need to speed up your test suite, consider using `.forClasses(...)` instead.
