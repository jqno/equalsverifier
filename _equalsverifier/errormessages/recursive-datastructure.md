---
title: "Recursive datastructure"
---
    Recursive datastructure.
    Add prefab values for one of the following types: com.example.Foo.

One of the fields in `Foo` is of type `Foo` itself.

    Recursive datastructure.
    Add prefab values for one of the following types: com.example.Foo, com.example.Bar.

One of the fields in `Foo` is of type `Bar`, and one of the fields in `Bar` is of type `Foo`.

EqualsVerifier will recursively try to instantiate objects of every class it encounters, but cannot do so if there is a loop in the object graph. In this case it's necessary to add predefined instances ("prefab values") of one of the classes involved in the loop. If EqualsVerifier mentions more than one class, it doesn't matter which one you choose, although if one of these classes is the class that you are testing, it is probably better to choose another.

Example:

{% highlight java %}
EqualsVerifier.forClass(MyClass.class)
    .withPrefabValues(Foo.class, new Foo(1), new Foo(2))
    .verify();
{% endhighlight %}

If `com.example.Foo` is a interface or an abstract class, it's fine to use instances from any implementation of `com.example.Foo`.

If you are using `withIgnoredFields`, you still have to add prefab values. EqualsVerifier will instantiate all fields, even if they are ignored, because it can't know if these fields aren't called in `equals` or `hashCode` anyway. For example, a class could contain a cache that is consulted in the `equals` method, even if it's not part of the equals contract.

Note that static fields are ignored only if they are also final.
