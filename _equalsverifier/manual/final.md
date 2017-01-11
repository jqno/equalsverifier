---
title: Making things final
---
The first error message that EqualsVerifier is likely to give you, is this:

> Subclass: equals is not final.
>
> Make your class or your equals method final, or supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.

The reason for this, is that it's very easy to make a subclass of your very carefully crafted class, which does something to mess up the symmetry or transitivity requirements of `equals`. When that happens, you risk unexpected behaviour, like not being able to look up a class in a `HashMap`.

(For specific examples of how that might happen, please read the chapter on `equals` in Josh Bloch's Effective Java, or [this article](http://www.artima.com/lejava/articles/equality.html) by Odersky, Spoon and Venners.)

The easiest way to make sure this kind of behaviour doesn't happen, is by making either your class or your `equals` method final.

If that's not an option, you're probably adding state in your subclasses that you want to include in `equals`. This is actually very hard to get right, and I talk more about it [here](/equalsverifier/manual/adding-state).

As a last resort, you can suppress the warning like this:

{% highlight java %}
EqualsVerifier.forClass(My.class)
    .suppress(Warning.STRICT_INHERITANCE)
    .verify();
{% endhighlight %}

If you do that, you should be aware that you do run the risk of unpleasant bugs caused by symmetry or transitivity issues.

