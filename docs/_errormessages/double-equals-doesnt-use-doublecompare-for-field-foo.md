---
title: "Double: equals doesn't use Double.compare for field foo"
---
You should never use `==` to compare doubles or floats in an `equals` method. Always use [`Double.compare`](http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#compare(double,%20double%29)) or [`Double.compareTo`](http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#compareTo(java.lang.Double%29)) instead.

Josh Bloch explains this in his book Effective Java. The short summary is that this method will do the right thing when confronted with `NaN` and positive and negative infinities. For example, `Float.NaN` is not equal to itself, but it has to be for `equals`, or you would never be able to retrieve it from a `HashMap`.

Should you use a delta function, such as `Math.abs(this.d - that.d) <= 0.0000001` to compare the values? In general, when comparing doubles, one should definitely do that, to avoid issues concerning rounding errors. In the case of `equals`, however, you will run into transitivity issues:

{% highlight java %}
double number = 0.0000001;
double delta = 2 * number;

double a = 1.0;
double b = 1.0 + number;
double c = 1.0 + number + number;

Math.abs(a - b) <= delta == true
Math.abs(b - c) <= delta == true
Math.abs(a - c) <= delta == false // violates transitivity
{% endhighlight %}

According the the transitivity requirement of `equals`, if `a.equals(b)` and `b.equals(c)`, `a.equals(c)` should also be true, which isn't the case when comparing deltas like this.

You will run into a similar problem when defining `hashCodes`: if two objects are equal to each other, their `hashCode`s must be equal as well. The only way to make this work, is to return a constant `hashCode`, which is undesirable.

Therefore, while you should in principle always compare doubles using a delta function, `equals` methods are an exception to this, and you should stick to using `Double.compare`.
