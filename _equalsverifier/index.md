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
* If you want to get started quickly, read the [getting started](/equalsverifier/manual/gettingstarted) page.
* If you want to learn more, read the [manual](/equalsverifier/manual).

If you're interested, there's more to read:

* The [FAQ](/equalsverifier/faq)
* The [changelog](/equalsverifier/changelog).
* The [migration guide](/equalsverifier/migration1to2), if you're migrating from version 1.x to 2.x.


Fork me on GitHub!
------------------
The source for this project is hosted on [GitHub](https://github.com/jqno/equalsverifier).

Pull Requests are welcome! But please also [open an issue](https://github.com/jqno/equalsverifier/issues) or [send a message to the Google Group](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier) so we can discuss it.

