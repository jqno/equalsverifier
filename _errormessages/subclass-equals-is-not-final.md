---
title: "Subclass: equals is not final"
---
    Subclass: equals is not final.
    Supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.

There are three ways to solve this error, in order of decreasing preference:

* Make the `equals` method (or even the whole class) final.
* If you intend your `equals` method to be overridden, and you also want subclasses to add state that needs to be included in the contract, things get complicated. In Item 8 of _Effective Java_, Josh Bloch argues that it is impossible to achieve this without breaking the contract. Nevertheless, it turns out to be possible. [This article](http://www.artima.com/lejava/articles/equality.html) by Martin Odersky, Bill Venners and Lex Spoon explains how to achieve this. If you decide to go down this path, you will need to supply EqualsVerifier with an example of a subclass with added state, like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .withRedefinedSubclass(SomeSubclass.class)
    .verify();
{% endhighlight %}

* Use `.suppress(Warning.STRICT_INHERITANCE)` to suppress the error message.
