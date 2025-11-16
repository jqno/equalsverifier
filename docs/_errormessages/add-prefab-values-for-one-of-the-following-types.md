---
title: "Add prefab values for one of the following types"
---
When testing a class, EqualsVerifier tries to instantiate it and provide values for all of its fields. Often, it can do that without help, but sometimes it isn't able to do that. In those cases, it will ask for 'prefab values'. For example:

    -> Recursive datastructure.
    Add prefab values for one of the following types: Foo.

In this situation, you can use `#withPrefabValues` or one of its variant methods to give EqualsVerifier some pre-built values for this type.

One counter-intuitive case is when you use `#withIgnoredFields()`, and EqualsVerifier asks for a prefab value for that field. This can happen because EqualsVerifier still wants to check that these fields can't cause `NullPointerException`s, and it wants to check that the field indeed doesn't participate in `equals`. For that, it needs values. Like with other fields, it will try to create them, but if it can't, EqualsVerifier _will_ ask for prefab values for these fields.

For more information about prefab values, read the [manual page about prefab values](/equalsverifier/manual/prefab-values).
