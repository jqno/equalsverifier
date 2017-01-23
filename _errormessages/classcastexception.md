---
title: "ClassCastException: java.lang.Object cannot be cast to ..."
---
There can be two causes for this error message. First, maybe you really did cast an `Object` to something it's not. Can't find the cast? Call EqualsVerifier with the `.debug()` method to see a full stacktrace.

Second, it might be a generics problem. In this case, it will always be `java.lang.Object` that cannot be cast to some other class. Consider the following (partial) class:

{% highlight java %}
final class StringReference {
	private final AtomicReference<String> stringRef;

	@Override
	public boolean equals(Object obj) {
		referToGenericParameter();
		
		if (!(obj instanceof StringReference)) {
			return false;
		}
		StringReference other = (StringReference)obj;
		return stringRef.equals(other.stringRef);
	}

	private void referToGenericParameter() {
		stringRef.get().length();
	}
}
{% endhighlight %}

In this case, EqualsVerifier will say "java.lang.Object cannot be cast to java.lang.String". EqualsVerifier can't see that the `AtomicReference`'s generic type parameter is `String` (due to [type erasure](http://download.oracle.com/javase/tutorial/java/generics/erasure.html)). Instead, it will just put in an `Object`. Normally, this isn't a problem, but in this case, a direct reference is made to a `String` method in `referToGenericParameter`. `Object` doesn't have a `length()` method, so the object inside the `AtomicReference` is (implicitly) cast to `String`. Since EqualsVerifier put in an `Object`, this fails.

How to fix this? It's easy. Just add a call to `withPrefabValues()`:

{% highlight java %}
EqualsVerifier.forClass(StringReference.class)
    .withPrefabValues(AtomicReference.class,
        new AtomicReference<String>("string"),
        new AtomicReference<String>("another string"))
    .verify();
{% endhighlight %}

EqualsVerifier will always look inside the pool of prefabricated values before building its own. So in this case, when it looks for instances of `AtomicReference`, it will find two `AtomicReference`s with `String`s already filled in. Problem solved!
