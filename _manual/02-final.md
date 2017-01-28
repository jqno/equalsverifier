---
title: Making things final
permalink: /manual/final/
---
The first error message that EqualsVerifier is likely to give you, is either this:

    Subclass: equals is not final.
    Make your class or your equals method final, or supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.

or this:

    Subclass: object is not equal to an instance of a trivial subclass with equal fields:
      Foo@123456
    Consider making the class final.

The reason for these messages is that it's very easy for your junior team member, or even for your six-months-later self, to make a subclass of your very carefully crafted class, which does something to mess up the symmetry or transitivity requirements of `equals`. When that happens, you risk unexpected behaviour, like not being able to look up a class in a `HashMap`.

(For specific examples of how that might happen, please read the chapter on `equals` in Josh Bloch's Effective Java, or [this article](http://www.artima.com/lejava/articles/equality.html) by Odersky, Spoon and Venners.)

The easiest way to make sure this kind of behaviour doesn't happen, is by making either your class or your `equals` method final.

If that's not an option, you're probably adding state in your subclasses that you want to include in `equals`. This is surprisingly hard to get right, and I talk more about it [here](/equalsverifier/manual/inheritance).

As a last resort, you can suppress the warning like this:

{% highlight java %}
EqualsVerifier.forClass(Foo.class)
    .suppress(Warning.STRICT_INHERITANCE)
    .verify();
{% endhighlight %}

If you do that, you should keep in mind that you can define a perfect `equals` method, but a subclass can still always break the `equals` contract, even for its superclass (which contains your perfect `equals` method)! It's very easy to override `equals` in a such way that breaks symmetry or transitivity with its superclass, even if you don't do it on purpose.

This is why EqualsVerifier makes such a big deal of this, and why suppressing `Warning.STRICT_INHERITANCE` is a last resort.

