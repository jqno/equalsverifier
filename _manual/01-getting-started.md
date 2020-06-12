---
title: Getting Started
permalink: /manual/getting-started/
---
## Get EqualsVerifier
Requirements:
* EqualsVerifier 3.x → Java 8 or higher
* EqualsVerifier 2.x → Java 7 or higher
* EqualsVerifier 1.x → Java 6 or higher

First, add the EqualsVerifier dependency to your build script. You can find the coordinates on [the homepage](/equalsverifier) and in [the README](https://github.com/jqno/equalsverifier/blob/main/README.md).

If you need to download the jar file directly, you can download it from [maven.org](http://search.maven.org/#search&#124;gav&#124;1&#124;g%3A%22nl.jqno.equalsverifier%22%20AND%20a%3A%22equalsverifier%22). EqualsVerifier doesn't have any transitive dependencies, so this jar is all you need.

## Use it in a test
This is what EqualsVerfier can look like in your test:

{% highlight java %}
@Test
public void equalsContract() {
    EqualsVerifier.forClass(Foo.class)
            .verify();
}
{% endhighlight %}

Chances are, EqualsVerifier will give you an error message on the first try. That might be frustrating at first, but there's a good reason for it. There's a surprising number of ways in which an `equals` method can contain bugs. Even when you let your IDE generate it, it might have problems.

These problems might not seem like a big deal, and to be honest, you might never encounter them anyway. But if you do, they can be incredibly hard to debug.

That's why EqualsVerifier's philosophy is to be super-strict by default. In fact, it can be frustratingly strict. But there's a lot of ways to tweak EqualsVerifier to make it behave just the way you want it to.

Here are the most common issues you might run into on your first try:

* [Making things final](/equalsverifier/manual/final)
* [Immutability](/equalsverifier/manual/immutability)
* [instanceof or getClass()](/equalsverifier/manual/instanceof-or-getclass)
* [Dealing with null](/equalsverifier/manual/null)

