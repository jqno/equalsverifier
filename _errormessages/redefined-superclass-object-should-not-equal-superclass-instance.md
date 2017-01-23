---
title: "Redefined superclass: object should not equal superclass instance"
---
This happens when `.withRedefinedSuperclass()` is called, but calling `superclass.equals(classUnderTest)` or `classUnderTest.equals(superclass)` is true. If the superclass and the class under test both override `equals`, and an instance of one is allowed to be equal to an instance of the other, there is no way to satisfy both the symmetry and transitivity requirements of the equals contract.

There are two ways to fix this. Either override equals in the class under test only, not in the superclass; or make sure an instance of the class under test can never be equal to an instance of the superclass by using the `canEqual` style of `equals` method definition. Note that the latter option is quite complex. For more information, read [this article](http://www.artima.com/lejava/articles/equality.html), Pitfall #4.
