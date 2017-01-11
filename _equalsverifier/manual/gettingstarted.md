---
title: Getting Started
---
## Get EqualsVerifier

* Maven users, add this to your POM:

{% highlight xml %}
<dependency>
    <groupId>nl.jqno.equalsverifier</groupId>
    <artifactId>equalsverifier</artifactId>
    <version>2.1.8</version>
    <scope>test</scope>
</dependency>
{% endhighlight %}

* ANT users, make sure the most recent EqualsVerifier jar is on the classpath. You can download it from [maven.org](http://search.maven.org/#search&#124;gav&#124;1&#124;g%3A%22nl.jqno.equalsverifier%22%20AND%20a%3A%22equalsverifier%22).

EqualsVerifier doesn't have any dependencies of its own.

## Use it in a test

{% highlight java %}
@Test
public void equalsContract() {
    EqualsVerifier.forClass(My.class).verify();
}
{% endhighlight %}

Chances are, EqualsVerifier will give you an error message on the first try. That might be frustrating at first, but there's a good reason for it. There's a surprising number of ways in which an `equals` method can contain bugs. Even when you let your IDE generate one, it might have problems.

These problems might not seem like a big deal, and to be honest, you might never encounter them anyway. But if you do, they can be incredibly hard to debug.

That's why EqualsVerifier's philosophy is to be super-strict by default. In fact, it can be frustratingly strict. But there's a lot of ways to tweak EqualsVerifier to make it behave just the way you want it to. Here are the most common ones:

* [Making things final](/equalsverifier/manual/final)
* 2

