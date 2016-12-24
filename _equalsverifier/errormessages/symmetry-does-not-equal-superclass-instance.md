---
title: "Symmetry: ... does not equal superclass instance ..."
---
Short explanation
-----------------
If you expected these to be equal, there is probably a bug in your `equals` method. If you expected them not to be equal, you can call `#withRedefinedSuperclass()` to silence the error.

Long explanantion
-----------------
There are two kinds of subclasses:

 * the kind that adds state, and maybe also behaviour.
 * the kind that adds only behaviour.

`ColoredPoint`, as described by Martin Odersky et al. in [this article](http://www.artima.com/lejava/articles/equality.html), is a clear example of a subclass that adds state. If you want to reflect this added state in its equals method, and still be able to make subclasses of `ColoredPoint` that can equal instances of `ColoredPoint` itself, you'll need to follow the advice laid out in the article and add a `canEqual` method. Importantly, this situation implies that an instance of `ColoredPoint` can *never* be equal to an instance of Point, even if their coordinates are the same.

However, if you don't need to add state, you won't override equals and you can do away with the added complexity of a `canEqual` method. More importantly, an instance of your class *should* be equal to an instance of its superclass if they have equal state. So the behaviour in this case is opposite to that of the subclass that adds state. (Also, a couple of other tests need to be performed in this scenario, as you can see in the code.)

Unfortunately, EqualsVerifier cannot guess which scenario applies for any given class. So for one of these scenarios, EqualsVerifier needs to run with a "custom setting".

EqualsVerifier defaults to the latter case, i.e. the one where only behaviour is added. In my experience, this is the case that occurs more often. Moreover, it often happens without the programmer even realising it! For example, Hibernate creates a subclass of every entity class.
A subclass that adds state, needs more ceremony: you'll have to add a `canEqual` method. Not many people know about this "trick", so it makes sense that this scenario occurs less often "in the wild". Therefore, this is the scenario that needs a custom setting: `#withRedefinedSuperclass()`.
