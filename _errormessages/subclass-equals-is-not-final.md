---
title: "Subclass: equals is not final"
---
    Subclass: equals is not final.
    Make your class or your equals method final, or supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.

There are three ways to solve this error, in order of decreasing preference:

* Make the `equals` method (or even the whole class) final.

* If you intend your `equals` method to be overridden, and you also want subclasses to add state that needs to be included in the contract, you have to use `withRedefinedSubclass`, as seen in the example below. If you decide to go down this path, I recommend reading the manual page about [inheritance](/equalsverifier/manual/inheritance).

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .withRedefinedSubclass(SomeSubclass.class)
    .verify();
{% endhighlight %}

* As a last resort, use `.suppress(Warning.STRICT_INHERITANCE)` to suppress the error message.
