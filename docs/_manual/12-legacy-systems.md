---
title: Dealing with legacy systems
permalink: /manual/legacy-systems/
---
When you're bringing a legacy system under test, you often don't want to mess with things that already work: it's more important to get some decent coverage. Once you have that, you can go back and make improvements.

That means that even though EqualsVerifier might fail on some (or many) of your classes, you don't want to change their `equals` methods just yet, because some part of the codebase might depend on that faulty implementation. At the same time, you do want to use EqualsVerifier, because you want to increase test coverage.

For these situations, there are several types of warnings that you can suppress:

* `Warning.ALL_FIELDS_SHOULD_BE_USED`: disables what it says it disables. You can be more specific with the methods `withIgnoredFields` and `withOnlyTheseFields`. Read more about this topic on the [page about ignoring fields](/equalsverifier/manual/ignoring-fields).
* `Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY`: allows the behavior in JPA where `equals` behaves differently when the id is set than when it is not set. Read more about this topic on the [page about JPA entities](/equalsverifier/manual/jpa-entities).
* `Warning.INHERITED_DIRECTLY_FROM_OBJECT`: disables the check that the class actually overrides `equals`. This can be useful when you scan a whole package or directory for classes and call EqualsVerifier in bulk.
* `Warning.NONFINAL_FIELDS`: disables the check that all fields are final. You can read more about this topic on the [page about final fields](/equalsverifier/manual/final).
* `Warning.NULL_FIELDS`: disables the check that all `equals` and `hashCode` methods have a null-check for non-primitive fields. You can be more specific with the method `withNonnullFields`. Read more about this topic on the [page about null](/equalsverifier/manual/null).
* `Warning.REFERENCE_EQUALITY`: allows `equals` methods to use `==` instead of `equals` on non-primitive fields.
* `Warning.STRICT_HASHCODE`: disables the check that all fields used in `equals` must also be used in `hashCode`.
* `Warning.STRICT_INHERITANCE`: disables the check that classes or their `equals` methods be final, or that inheritance is properly accounted for. Read more about this topic on the [page about inheritance](/equalsverifier/manual/inheritance).
* `Warning.TRANSIENT_FIELDS`: disables the check that transient fields do not participate in `equals`. This applies both to Java's `transient` keyword, which applies to serialization, and to JPA's `@Transient` annotation, which applies to, well, JPA.
* `Warning.BIGDECIMAL_EQUALITY`: disables the check that equality of `BigDecimal` fields is implemented using `compareTo` rather than `equals`. Read more about this topic on the [page about BigDecimal equality](/equalsverifier/errormessages/bigdecimal-equality).

Of course, once you have sufficient test coverage, you _will_ come back and fix these issues, right? 😉

