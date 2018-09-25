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
EqualsVerifier.forClass(Foo.class)
    .withPrefabValues(Bar.class, new Bar(1), new Bar(2))
    .verify();
{% endhighlight %}

If `Bar` is an interface or an abstract class, it's fine to use instances from any implementation of `Foo`.

If you are using `withIgnoredFields`, you still have to add prefab values. EqualsVerifier will instantiate all fields, even if they are ignored, because it can't know if these fields aren't called in `equals` or `hashCode` anyway. For example, a class could contain a cache that is consulted in the `equals` method, even if it's not part of the equals contract.

Note that static fields are ignored only if they are also final.

<a name="generics"></a>

Generics
--------
In rare cases involving generics, `withPrefabValues` might not work.

For instance, Android has a `SparseArray` type that has an `equals` method that iterates over its elements and assigns each one to a variable of its generic type. If you have a class with two `SparseArray`s with different generic parameters that are both checked by `equals`, EqualsVerifier will fail. For example:

{% highlight java %}
class SparseArrayContainer {
    private final SparseArray<String> strings;
    private final SparseArray<Integer> ints;

    // leaving out everything else for brevity
}

// ...

EqualsVerifier.forClass(SparseArrayContainer.class)
    .withPrefabValues(SparseArray.class, new SparseArray(1), new SparseArray(2, 3))
    .verify();
{% endhighlight %}

This will fail because there's no prefab values for the `String` variant of `SparseArray`.

In these cases, you can use `withGenericPrefabValues`:

{% highlight java %}
EqualsVerifier.forClass(SparseArrayContainer.class)
    .withGenericPrefabValues(SparseArray.class, SparseArray::new)
    .verify();
{% endhighlight %}

In this case, EqualsVerifier will generate values of type `Integer` and `String`, and provide them to the lambda which can use that value to construct a `SparseArray` of the correct generic type.

