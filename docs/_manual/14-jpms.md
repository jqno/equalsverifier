---
title: "The Java Platform Module System"
permalink: /manual/jpms/
---
EqualsVerifier is compatible with the Java Platform Module System (JPMS). However, since it does some reflection, you have to open up some packages. Perhaps you have already done so, as test frameworks like JUnit also require this.

The recommended approach is to put a `module-info.java` file in your `src/test/java` folder, that copies the content the `module-info.java` file in `src/main/java`. Let's say this is your `src/main/java/module-info.java`:

{% highlight java %}
module my.module {
    exports my.module;
}
{% endhighlight %}

The easiest way to use EqualsVerifier, is to put this `module-info.java` in your `src/test/java` folder:

{% highlight java %}
open module my.module {              // Note: open
    exports my.module;               // Same as before

    requires org.junit.jupiter.api;  // For JUnit
    requires nl.jqno.equalsverifier; // For EqualsVerifier
    requires net.bytebuddy;          // Dependency of EqualsVerifier
}
{% endhighlight %}

Note that this approach opens up the entire module for reflection. If you do not want this, even in your tests, you can be more precise in what you open up:

{% highlight java %}
open module my.module {
    exports my.module;
    opens my.module.package.model;   // Open model package

    requires org.junit.jupiter.api;
    requires nl.jqno.equalsverifier;
    requires net.bytebuddy;
}
{% endhighlight %}

Note that the line `requires net.bytebuddy` is not necessary if you use the uberjar dependency `equalsverifier-nodep`.

Note that if you do this, and you have model classes or dependencies for model classes in other packages, you will have to open these packages as well, or provide prefab values for these dependencies:

{% highlight java %}
import my.module.package.somewhere.inaccessible.Bar;

EqualsVerifier.forClass(Foo.class)
    .withPrefabValues(Bar.class, new Bar(1), new Bar(2))
    .verify();
{% endhighlight %}

When the class that EqualsVerifier is testing is inaccessible, you will get this error message:

    The class is not accessible via the Java Module system.
    Consider opening the module that contains it.

If the class is accessible, but the class for one of its fields isn't, you will get this error message:

    Field foo of type Bar is not accessible via the Java Module System.
    Consider opening the module that contains it, or add prefab values for type Bar.

