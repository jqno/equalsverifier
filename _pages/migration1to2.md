---
title: Migration guide 1 to 2
permalink: /migration1to2/
---
When migrating from EqualsVerifier 1 to 2, there are a few things you should be aware of. (See also the [changelog](/equalsverifier/changelog)).

* EqualsVerifier 2 no longer supports Java 6. Don't upgrade if you're still using Java 6.
* `EqualsVerifier.forExamples` has been removed, because over time, EqualsVerifier has been ignoring the given examples more and more anyway, favouring `forClass` style checks instead. There were also occasional problems when the given examples were of inconsistent  types. Therefore, in almost all cases, you can use `EqualsVerifier.forClass` instead. In the rare cases when that doesn't work, you can also use `EqualsVerifier.forRelaxedEqualExamples`.
* EqualsVerifier 1 has a switch `allFieldsShouldBeUsed()`, which has now become the default behaviour. This means that EqualsVerifier expects that all non-static fields in the class are actually used in the `equals` method, and fails if this isn't the case. In most cases, this should not matter. If you do have fields in your class that shouldn't be used in `equals`, you can tell EqualsVerifier to ignore them individually by calling `#withIgnoredFields`, or revert to the old behaviour by suppressing `Warning.ALL_FIELDS_SHOULD_BE_USED`.
* EqualsVerifier now fails if you don't actually override `equals` in your class, because this might signal that you intend to override `equals`, but forgot. If this is not the case (for instance, if you 'sweep' a whole package where most classes override `equals` but one doesn't), you can revert to the old behaviour by suppressing `Warning.INHERITED_DIRECTLY_FROM_OBJECT`.
* EqualsVerifier is now smarter when it comes to type erasure. In many cases where you had to call `withPrefabValues` for a generic type to work around a `ClassCastException` in EqualsVerifier, this is no longer necessary. An example of this is Android's `SparseArray` class.

If you encounter any other problems, please let me know on the [mailing list](https://groups.google.com/forum/?fromgroups#!forum/equalsverifier).

