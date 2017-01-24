---
title: Relaxed equality
permalink: /manual/relaxed-equality/
---
Sometimes, simply comparing all the values of the fields is not the best way to define `equals`. The famous example of this is rational numbers, where <sup>1</sup>/<sub>2</sub> and <sup>2</sup>/<sub>4</sub> are equal, even though they have different fields. EqualsVerifier calls this 'relaxed equality'.

The best way to explain how to deal with this, is by showing an example. Let's say you have a Rational class:

{% highlight java %}
class Rational {
    private final int numerator;
    private final int denominator;

    public equals(Object obj) {
        if (!(obj instanceof Rational)) {
            return false;
        }
        Rational other = (Rational)obj;
        return (numerator / denominator) == (other.numerator / other.denominator);  // awfully bad implementation but you get the idea
    }

    // leaving out everything else for brevity
}
{% endhighlight %}

Then you can write your test like this:

{% highlight java %}
@Test
public void testEquality() {
    Rational a = new Rational(1, 2);
    Rational b = new Rational(2, 4);
    Rational x = new Rational(1, 3);
    EqualsVerifier.forRelaxedEqualExamples(a, b)
        .andUnequalExample(x)
        .verify();
}
{% endhighlight %}

As you can see, you have to give EqualsVerifier some example objects for your class. First, you have to give at least two examples that are equal to each other, but that have different values for their fields. In this case, we give the <sup>1</sup>/<sub>2</sub> and <sup>2</sup>/<sub>4</sub> from our example above. You can give more than two examples, if you need to.

Second, you should give an example that's unequal to the objects you gave before. In this case, we give <sup>1</sup>/<sub>3</sub>. You can give more than one example, if you need to. EqualsVerifier does the obvious sanity checks: no duplications are allowed.

That's all there is to it!

