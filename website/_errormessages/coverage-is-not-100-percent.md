---
title: "Coverage is not 100%"
---
EqualsVerifier should get 100% code coverage on your `equals` and `hashCode` methods. However, it can happen that it doesn't achieve this. Below is a list of cases where it's impossible to get 100% coverage, and their solutions.

If you have an example of a class where EqualsVerifier doesn't give you 100% coverage, and it's not in the list below, please [let me know](/equalsverifier/help).


Using Lombok
---
Lombok always generates null checks in its `equals` methods, even if there is an `@Nonnull` annotation. For example:

{% highlight java %}
@EqualsAndHashCode
public static final class Lombok {
    @Nonnull
    private final String s;

    public Lombok(String s) {
        this.s = s;
    }
}
{% endhighlight %}

This class will have less than 100% coverage, because null checks are generated for the `s` field. EqualsVerifier will not check these paths, due to the `@Nonnull` annotation.

If you run into this problem, you can tell EqualsVerifier to ignore the annotation, like this:

{% highlight java %}
EqualsVerifier.forClass(Lombok.class)
    .withIgnoredAnnotations(Nonnull.class)
    .verify();
{% endhighlight %}


Using canEqual
---
If you have a hierarchy of classes that each redefine `equals` and `hashCode` as described in [this article](http://www.artima.com/lejava/articles/equality.html), the leaf nodes in your class hierarchy tree won't get 100% percent coverage.

Say that you have a hierarchy of `Point` classes, where `Point` has fields `x` and `y`, and its subclass `ColorPoint` adds a field `color`. `ColorPoint`'s `canEqual` method looks like this:

{% highlight java %}
@Override
public boolean canEqual(Object obj) {
    return obj instanceof ColorPoint;
}
{% endhighlight %}

And its `equals` method looks like this:

{% highlight java %}
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof ColorPoint)) {
        return false;
    }
    ColorPoint other = (ColorPoint)obj;
    if (!other.canEqual(this)) {
        return false;
    }
    return super.equals(other) && color.equals(other.color);
}
{% endhighlight %}

Now, the `return false;` statement after the call to `other.canEqual(this)` will get no coverage. This makes sense, because it can only be reached if `other.canEqual(this)` returns false, which it will never do. Because of the `instanceof` check in `equals`, `canEqual` will always be called on a (subclass of) `ColorPoint`. But `ColorPoint` is a leaf node, so there are no subclasses. `other` will always be _exactly_ of type `ColorPoint`, and `other.canEqual(this)` will always return true.

There are two ways to work around this, and still get 100% coverage:

* Simply remove the `canEqual` check in the leaf nodes. I don't recommend this, because it's risky if you later decide to add subclasses to `ColorPoint`; then it will no longer be a leaf node in the tree. If you forget to put back the call to `canEqual`, you may run into problems.
* Invent a subclass specifically for your test. It's verbose, and `ColorPoint` can no longer be marked `final`, but it's safe and correct. Your test will now look like this:

{% highlight java %}
@Test
public void leafNodeEquals() {
    class EndPoint extends ColorPoint {
        public EndPoint(int x, int y, Color color) {
            super(x, y, color);
        }

        @Override
        public boolean canEqual(Object obj) {
            return false;
        }
    }

    EqualsVerifier.forClass(ColorPoint.class)
            .withRedefinedSuperclass()
            .withRedefinedSubclass(EndPoint.class) // Don't forget to add this line
            .verify();
}
{% endhighlight %}

Note that this issue is not specific to EqualsVerifier; with hand-written `equals` test code you would run into exactly the same problem.


Non-standard equality code
---
Another way that EqualsVerifier won't reach 100% code coverage, is with non-standard equality code. For example, given a `Point` class with fields `x` and `y`:

{% highlight java %}
@Override
public boolean equals(Object obj) {
    if (!(obj instanceof Point)) {
        return false;
    }
    if (x == 42) {
        return false;
    }
    Point other = (Point)obj;
    return x == other.x && y == other.y;
}
{% endhighlight %}

EqualsVerifier will never execute the second `if` statement's block, simply because it doesn't test for all possible values. It can't; it would take far too long. (For our `Point` class, assuming that `x` and `y` are `int`, it would take more than 2<sup>32</sup>*2<sup>32</sup> comparisons.)

Of course, this is a contrived example (and many more are possible, for example using random numbers or environment variables). However, if you really do need non-standard branches in your `equals` method, you will have to test them manually; not just because EqualsVerifier doesn't _cover_ them, but because EqualsVerfier also doesn't _test_ them.

