---
title: EqualsVerifier
permalink: /
tagline: "Makes testing equals() and hashCode() in Java a one-liner!"
layout: splash
header:
  overlay_color: "#263238"
imagebutton_row:
  - image_path: "images/getting-started.jpg"
    title: "Getting Started"
    url: "manual/getting-started/"
  - image_path: "images/manual.jpg"
    title: "Manual"
    url: "manual/"
  - image_path: "images/error-messages.jpg"
    title: "Error messages explained"
    url: "errormessages/"
  - image_path: "images/resources.jpg"
    title: "Additional resources"
    url: "resources/"
---
{% include imagebutton_row %}

Have you ever written an `equals` method, along with five screens of unit tests to test it? Or worse: not bothered to test it at all, because "the IDE generates it anyway"?

EqualsVerifier helps you. Testing `equals` can be as simple as:

{% highlight java %}
@Test
public void equalsContract() {
    EqualsVerifier.forClass(Foo.class).verify();
}
{% endhighlight %}

EqualsVerifier is an opinionated library, which means that it can be quite strict. If you feel it's too much, you can make it more lenient:

{% highlight java %}
@Test
public void simpleEqualsContract() {
    EqualsVerifier.simple().forClass(Foo.class).verify();
}
{% endhighlight %}

And EqualsVerifier even gives you 100% coverage on `equals` and `hashCode` methods.

Don't forget to add it to your build!

{% include maven %}

## Prefer to watch a short video?

<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/ivRjf8yvVMk" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

<small>Video by [Tom Cools](https://twitter.com/TCoolsIT)</small>

## A note on equality

EqualsVerifier cares about bug-free equality, in Java and in real life. The place where a person happens to be born, the colour of their skin, their gender, or the person they happen to love, must not affect the way they are treated in life. If it does, that's a bug and it should throw an error.

Don't allow bugs in your equality.

🌈🧑🏻‍🤝‍🧑🏾🌍
