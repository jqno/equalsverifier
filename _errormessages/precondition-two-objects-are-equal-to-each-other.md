---
title: "Precondition: two objects are equal to each other"
---
This happens when two instances of the type under test, whose fields have different values, are still equal to each other. This usually happens when none of the type's fields influences the result of the call to `equals`.

The main example of this situation occurs in singleton objects. EqualsVerifier cannot (and should not) be used to test singletons; by their very nature, there should be one and only one instance of a singleton in the system, whereas `equals` is only useful when two or more instances are around. In other words, there is no point in testing `equals` on a singleton.
