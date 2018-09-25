---
title: Migration guide 2 to 3
permalink: /migration2to3/
---
When migrating from EqualsVerifier 2 to 3, there are a few things you should be aware of. (See also the [changelog](/equalsverifier/changelog#3.x)).

* EqualsVerifier 3 no longer supports Java 7. Don't upgrade if you're still using Java 7.
* `Warning.ANNOTATION` has been removed, as it's no longer needed: EqualsVerifier can now handle any bytecode reading issues by itself. You can safely remove the warning; it won't affect your code or how the EqualsVerifier test is run.
* The content and layout of error messages is slightly different. If you have tools that depend on this, make sure these still work. You could also consider using `EqualsVerifier#report()` instead of `EqualsVerifier#verify()`.
* EqualsVerifier is now smarter when it comes to type erasure. In rare cases, a `ClassCastException` would be thrown by EqualsVerifier when it couldn't figure out the correct generic types. You can now fix those with `#withGenericPrefabValues()`. See [this page](/equalsverifier/errormessages/recursive-datastructure#generics) for more information.

If you encounter any other problems, please let me know on the [mailing list](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier).

