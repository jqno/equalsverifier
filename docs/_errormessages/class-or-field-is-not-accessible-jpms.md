---
title: "Class or field is not accessible via the Java Module System"
---
    The class is not accessible via the Java Module system.
    Consider opening the module that contains it.

<br/>

    Field foo of type Bar is not accessible via the Java Module System.
    Consider opening the module that contains it, or add prefab values for type Bar.

Either the class that EqualsVerifier is testing, or one of its fields, cannot be accessed due to restrictions from the Java Platform Module System.

If it's the class that EqualsVerifier is testing, you must modify your `module-info.java` such that the package containing the class, is accessible for reflection via the `open` keyword.

If it's one of the fields in the class, you can either modify your `module-info.java` as above, or add prefab values for the given type:


{% highlight java %}
EqualsVerifier.forClass(TheClassThatImTesting.class)
    .withPrefabValues(Bar.class, new Bar(1), new Bar(2))
    .verify();
{% endhighlight %}

See also the [section in the manual](/equalsverifier/manual/jpms) about modules.
