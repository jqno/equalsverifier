package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class ExamplesChecker<T> implements Checker {

    private final Class<T> type;
    private final List<T> equalExamples;
    private final List<T> unequalExamples;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public ExamplesChecker(Configuration<T> config) {
        this.type = config.getType();
        this.equalExamples = config.getEqualExamples();
        this.unequalExamples = config.getUnequalExamples();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkPreconditions();

        for (int i = 0; i < equalExamples.size(); i++) {
            T reference = equalExamples.get(i);
            checkSingle(reference);

            for (int j = i + 1; j < equalExamples.size(); j++) {
                T other = equalExamples.get(j);
                checkEqualButNotIdentical(reference, other);
                checkHashCode(reference, other);
            }
        }

        for (T reference : unequalExamples) {
            checkSingle(reference);
        }
    }

    private void checkPreconditions() {
        for (T example : equalExamples) {
            assertTrue(
                Formatter.of(
                    "Precondition:\n  %%\nand\n  %%\nare of different classes",
                    equalExamples.get(0),
                    example
                ),
                type.isAssignableFrom(example.getClass())
            );
        }
    }

    private void checkEqualButNotIdentical(T reference, T other) {
        assertFalse(
            Formatter.of("Precondition: the same object appears twice:\n  %%", reference),
            reference == other
        );
        assertFalse(
            Formatter.of("Precondition: two identical objects appear:\n  %%", reference),
            isIdentical(reference, other)
        );
        assertTrue(
            Formatter.of(
                "Precondition: not all equal objects are equal:\n  %%\nand\n  %%",
                reference,
                other
            ),
            reference.equals(other)
        );
    }

    private void checkSingle(T reference) {
        final T copy = ObjectAccessor.of(reference, type).copy();

        checkReflexivity(reference);
        checkNonNullity(reference);
        checkTypeCheck(reference);
        checkHashCode(reference, copy);
    }

    private void checkReflexivity(T reference) {
        try {
            assertEquals(
                Formatter.of("Reflexivity: object does not equal itself:\n  %%", reference),
                reference,
                reference
            );
        } catch (ClassCastException e) {
            Formatter f = Formatter.of(
                "Generics: ClassCastException was thrown. Consider using" +
                " withGenericPrefabValues for the type that triggered the" +
                " exception."
            );
            fail(f, e);
        }
    }

    @SuppressFBWarnings(
        value = "EC_NULL_ARG",
        justification = "Check what happens when null is passed into equals."
    )
    private void checkNonNullity(T reference) {
        try {
            boolean nullity = reference.equals(null);
            assertFalse(Formatter.of("Non-nullity: true returned for null value"), nullity);
        } catch (NullPointerException e) {
            fail(Formatter.of("Non-nullity: NullPointerException thrown"), e);
        }
    }

    private void checkTypeCheck(T reference) {
        SomethingElse somethingElse = new SomethingElse();
        try {
            Formatter f = Formatter.of(
                "Type-check: equals returns true for an unrelated type.\n" +
                "Add an instanceof or getClass() check."
            );
            assertFalse(f, reference.equals(somethingElse));
        } catch (AssertionException e) {
            throw e;
        } catch (ClassCastException e) {
            Formatter f = Formatter.of(
                "Type-check: equals throws ClassCastException.\n" +
                "Add an instanceof or getClass() check."
            );
            fail(f, e);
        } catch (Exception e) {
            Formatter f = Formatter.of(
                "Type-check: equals throws %%.\nAdd an instanceof or getClass() check.",
                e.getClass().getSimpleName()
            );
            fail(f, e);
        }
    }

    private void checkHashCode(T reference, T copy) {
        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        assertEquals(
            Formatter.of(
                "hashCode: hashCode should be consistent:\n  %% (%%)",
                reference,
                referenceHashCode
            ),
            referenceHashCode,
            cachedHashCodeInitializer.getInitializedHashCode(reference)
        );

        if (!reference.equals(copy)) {
            return;
        }

        int copyHashCode = cachedHashCodeInitializer.getInitializedHashCode(copy);
        Formatter f = Formatter.of(
            "hashCode: hashCodes should be equal:\n  %% (%%)\nand\n  %% (%%)",
            reference,
            referenceHashCode,
            copy,
            copyHashCode
        );
        assertEquals(f, referenceHashCode, copyHashCode);
    }

    private boolean isIdentical(T reference, T other) {
        return rethrow(
            () -> {
                for (Field field : FieldIterable.of(reference.getClass())) {
                    field.setAccessible(true);
                    if (!Objects.equals(field.get(reference), field.get(other))) {
                        return false;
                    }
                }

                return true;
            }
        );
    }

    @SuppressFBWarnings(
        value = "HE_HASHCODE_USE_OBJECT_EQUALS",
        justification = "The hashCode must be stable, the class has no state so we don't need to" +
        " override equals"
    )
    private static final class SomethingElse {

        @Override
        public int hashCode() {
            // Must return a stable, high-value hashCode,
            // otherwise the SymmetryTest (which depends on the hashCode) becomes flaky.
            return Integer.MAX_VALUE;
        }
    }
}
