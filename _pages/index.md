---
title: EqualsVerifier
permalink: /
---
Have you ever written an `equals` method, along with five screens of unit tests to test it? Or worse: not bothered to test it at all, because "the IDE generates it anyway"?

EqualsVerifier helps you. Testing `equals` can be as simple as:

{% highlight java %}
@Test
public void equalsContract() {
    EqualsVerifier.forClass(Foo.class).verify();
}
{% endhighlight %}

And EqualsVerifier even gives you 100% coverage on `equals` and `hashCode` methods.


Sounds great, how do I use it?
---
Add it to your build like this (please adjust for your build system of choice):

{% highlight xml %}
<dependency>
    <groupId>nl.jqno.equalsverifier</groupId>
    <artifactId>equalsverifier</artifactId>
    <version>3.1.12</version>
    <scope>test</scope>
</dependency>
{% endhighlight %}

* If you want to get started quickly, read the [getting started](/equalsverifier/manual/getting-started) page.
* If you want to learn more, read the [manual](/equalsverifier/manual), or watch the video:
  <iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/pNJ_O10XaoM?rel=0" frameborder="0" allowfullscreen></iframe>

If you're interested, there's more to read:

* The [FAQ](/equalsverifier/faq).
* The [changelog](https://github.com/jqno/equalsverifier/blob/master/CHANGELOG.md).
* The migration guides:
    * [2.x to 3.x](/equalsverifier/migration2to3)
    * [1.x to 2.x](/equalsverifier/migration1to2)
* The [inspiration](/equalsverifier/inspiration) for EqualsVerifier.


Fork me on GitHub!
---
The source for this project is hosted on [GitHub](https://github.com/jqno/equalsverifier).

Pull Requests are welcome! But please also [open an issue](https://github.com/jqno/equalsverifier/issues) or [send a message to the Google Group](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier) so we can discuss it.

