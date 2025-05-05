---
title: Migration guide 3 to 4
permalink: /migration3to4/
---
When migrating from EqualsVerifier 3 to 4, there are a few things you should be aware of. (See also the [changelog](https://github.com/jqno/equalsverifier/blob/main/CHANGELOG.md#4.x)).

* EqualsVerifier 3 no longer supports Java 8. Don't upgrade if you're still using Java 8 or 11. The new minimal Java version is 17.
* EqualsVerifier is now fully modularized. If you run your tests on the modulepath, see [the manual entry on JPMS](/equalsverifier/manual/jpms).
* A number of previously deprecated methods have now been removed:
  * `#withResetCaches()`: you can simply remove this; caches are always reset automatically.
  * `Warning.ZERO_FIELDS`: use `EqualsVerifier.forExamples()` or `#withPrefabValuesForField()` instead.
  * Various overloads of `EqualsVerifier.forPackage()`: you can use the new overload that takes a `ScanOption` parameter: here you will find the functionality you need. See [the changelog for version 3.19](https://github.com/jqno/equalsverifier/blob/main/CHANGELOG.md#ScanOption) for more details.
  * `EqualsVerifier.forPackage().except()`: this functionality can now also be accessed through the `ScanOption` parameter.
* EqualsVerifier now integrates with Mockito to create prefab values. If you already use it, EqualsVerifier will pick it up automatically. If you don't use it, you might consider adding it as a dependency to your project if you have to create a lot of manual prefab values. If it causes problems, use `#set(Mode.skipMockito())` to disable it (and consider [opening an issue](https://github.com/jqno/equalsverifier/issues)).
* Built-in prefab values for Google Guava, Joda-Time and Javafx were removed, because it seems they aren't used as much anymore as they used to be. If you still use them and this is a problem, you can [add prefab values](/equalsverifier/manual/prefab-values) for them, or rely on the new Mockito integration.

If you encounter any other problems, please let me know in the [issue tracker](https://github.com/jqno/equalsverifier/issues).
