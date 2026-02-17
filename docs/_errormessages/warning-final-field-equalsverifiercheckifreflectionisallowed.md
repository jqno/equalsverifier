---
title: "WARNING: Final field equalsVerifierCheckIfReflectionIsAllowed…"
---
    WARNING: Final field equalsVerifierCheckIfReflectionIsAllowed in class nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory$1C has been mutated reflectively by class nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory in unnamed module @3cdf2c61 (file:/home/jqno/w/equalsverifier/equalsverifier/equalsverifier-core/target/classes/)
    WARNING: Use --enable-final-field-mutation=ALL-UNNAMED to avoid a warning
    WARNING: Mutating final fields will be blocked in a future release unless final field mutation is enabled

You are using Java 26 or higher. With [JEP 500: "Prepare to Make Final Mean Final"](https://openjdk.org/jeps/500), Java issues warnings when final fields are mutated. In some later version of Java (it is not yet known which version), these warnings will turn into exceptions. If you use `--illegal-final-field-mutation=deny`, these warnings will also turn into exceptions.

The warning is EqualsVerifier checking if final fields can be mutated, by mutating a final field named `equalsVerifierCheckIfReflectionIsAllowed`. If that throws an exception, EqualsVerifier knows to take a different path and ask the user for factories.

If EqualsVerifier didn't do this check, a similar warning on a different field would occur later in the test, when EqualsVerifier mutates a final field on the class that you're testing.

See [the manual entry on "final means final"](/equalsverifier/manual/final-means-final) for more details.
