---
title: Adding state to subclasses
---
Sometimes you want to override a class, add a field, and override `equals` to look at the new field. If you don't forget to call `super.equals()` it should be fine, right? Well, it depends.

If you [use `getClass`](/equalsverifier/manual/instanceof-or-getclass), and you're not concerned with the Liskov substitution principle, you're probably fine.

However, if you use `instanceof`, you have to take care that you don't break the symmetry requirement of `equals`. The classic example of that, is that you have a Point class with `x` and `y` coordinates, and you create a ColorPoint subclass that adds colour. If you just call `super.equals()`, you will end up in a situation where symmetry is broken:

{% highlight java %}
Point p = new Point(1, 1);
ColorPoint cp = new ColorPoint(1, 1, RED);

p.equals(cp) // returns true
cp.equals(p) // returns false
{% endhighlight %}

If you want to know how to solve this, I recommend that you read Odersky, Spoon & Venners's excellent article [How to write an equality method in Java](http://www.artima.com/lejava/articles/equality.html). In the "Pitfall 4" section, they describe how you can add a `canEqual` method to solve this issue, while maintaining the Liskov Substitution Principle.

Once you have done this, you need to tell EqualsVerifier about it. You have to test both the superclass (Point) and the subclasses (ColorPoint). Let's start with Point:

{% highlight java %}
EqualsVerifier.forClass(Point.class)
    .withRedefinedSubclass(ColorPoint.class)
    .verify();
{% endhighlight %}

By calling `withRedefinedSubclass`, you tell EqualsVerifier that you've designed your class to be overridden, and that you intend for the subclasses to add state. In other words, that Point can't be final, and neither can its `equals` method. For EqualsVerifier to test this, it needs access to a subclass where this is the case. Since it can't invent one by itself, you have to give it one. In this case, we already have one: ColorPoint. Note that this doesn't mean that ColorPoints `equals` method is also tested. You still need to do that separately:

{% highlight java %}
EqualsVerifier.forClass(ColorPoint.class)
    .withRedefinedSuperclass()
    .verify();
{% endhighlight %}

Again, you need to tell EqualsVerifier that the class is part of an inheritance hierarchy. Because ColorPoint is the subclass, EqualsVerifier needs access to the superclass. Since there can be only one superclass, EqualsVerifier can find it by itself, and you don't have to supply it like you did for Point.

Since ColorPoint is at the bottom of the inheritance hierarchy, EqualsVerifier will ask you to make it final, or to make its `equals` and `hashCode` methods final, just like it does for regular classes.

If, instead, you want ColorPoint to be subclassed further, you have to give EqualsVerifier another subclass, like so:

{% highlight java %}
EqualsVerifier.forClass(ColorPoint.class)
    .withRedefinedSuperclass()
    .withRedefinedSubclass(EnhancedColorPoint.class)
    .verify();
{% endhighlight %}

Then of course, you also need to test EnhancedColorPoint.

All of this is quite complicated, and often not necessary. This is why EqualsVerifier suggests by default that you [make things final](/equalsverifier/manual/final).

