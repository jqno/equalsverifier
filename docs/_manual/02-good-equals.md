---
title: What does a good equals method look like?
permalink: /manual/good-equals
---
EqualsVerifier checks if you wrote your `equals` method the right way, but what _is_ the right way?

Let's say we have a class that looks like this:

{% highlight java %}
public class Person {
    private final String name;
    private final int age;

    public Point(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // equals?
    // hashCode?
}
{% endhighlight %}

## `equals`

A good `equals` method for this class looks like this:

{% highlight java %}
    public boolean equals(Object obj) {
        return obj instanceof Person other &&
                Objects.equals(name, other.name) &&
                age == other.age;
    }
{% endhighlight %}

This makes use of Java 17's pattern matching for instanceof feature. `java.util.Objects.equals(a, b)` takes care of null checks for non-primitive fields.

In prior versions of Java, it would look like this:

{% highlight java %}
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return Objects.equals(name, other.name) &&
                age == other.age;
    }
{% endhighlight %}

IDEs often generate an an instance check as the first line:

{% highlight java %}
        if (this == obj) return true;
{% endhighlight %}

This is intended as an optimization. However, it turns out that, due to predictive branching optimizations that the JVM performs, this line actually makes `equals` slower in most cases, not faster. Therefore, I recommend leaving it out. See [this video](https://www.youtube.com/watch?v=kuzjX_efuDs) for more details.

## `hashCode`

If you override `equals` in a class, you should always override `hashCode` as well. A good `hashCode` method for our `Person` class looks like this:

{% highlight java %}
    public int hashCode() {
        return Objects.hash(name, age);
    }
{% endhighlight %}

## Resources

If you want to know the reasoning behind all this, I recommend these resources:

{% include good-equals %}
