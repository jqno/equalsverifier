---
title: EqualsVerifier
permalink: /
description: "Makes testing equals() and hashCode() in Java a one-liner!"
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

If you feel EqualsVerifier is too strict, you can make it more lenient:

{% highlight java %}
@Test
public void simpleEqualsContract() {
    EqualsVerifier.simple().forClass(Foo.class).verify();
}
{% endhighlight %}

And EqualsVerifier even gives you 100% coverage on `equals` and `hashCode` methods.

Don't forget to add it to your build!

{% include maven %}
