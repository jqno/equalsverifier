---
title: "Mutability: equals depends on mutable field"
---
Your `equals` method references a field that is not final. This means that if the value of this field changes, the same object may not be equal to itself at two different points in time. This can be a problem if you use this object as a key in a map, or if you put it in a `HashSet` or other hash-based collection: the collection might not be able to find your object again.

There are three ways to solve this:

* Make your field final. This is the preferred solution.
* Add an `@Immutable` annotation to your class if you _know_ that the class is immutable, but the fields cannot be final for some reason.
* Suppress `Warning.NONFINAL_FIELDS` if your class has to be mutable. This is not recommended.

Note: Unfortunately, EqualsVerifier cannnot detect if your fields are truly immutable, which is what's actually needed here. It can only see if fields are final, or if the class has an `@Immutable` annotation. 

Read more about this subject in the manual's page on [immutability](/equalsverifier/manual/immutability).

