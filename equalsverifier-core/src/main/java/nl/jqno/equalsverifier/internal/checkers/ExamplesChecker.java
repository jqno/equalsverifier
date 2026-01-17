package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.util.*;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class ExamplesChecker<T> implements Checker {

    private final Class<T> type;
    private final List<T> equalExamples;
    private final List<T> unequalExamples;
    private final SubjectCreator<T> subjectCreator;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public ExamplesChecker(Context<T> context) {
        Configuration<T> config = context.getConfiguration();
        this.type = config.type();
        this.equalExamples = config.equalExamples();
        this.unequalExamples = config.unequalExamples();
        this.subjectCreator = context.getSubjectCreator();
        this.cachedHashCodeInitializer = config.cachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkPreconditions();

        for (int i = 0; i < equalExamples.size(); i++) {
            T reference = equalExamples.get(i);
            checkSingle(reference, reference);

            for (int j = i + 1; j < equalExamples.size(); j++) {
                T other = equalExamples.get(j);
                checkEqualButNotIdentical(reference, other);
                checkHashCode(reference, other);
            }
        }

        List<Tuple<T>> unequals = ensureEnoughExamples(unequalExamples);
        for (var tuple : unequals) {
            checkSingle(tuple.red(), tuple.blue());
        }
    }

    private List<Tuple<T>> ensureEnoughExamples(List<T> examples) {
        if (examples.size() > 0) {
            return examples.stream().map(e -> new Tuple<>(e, e, e)).toList();
        }

        var result = new ArrayList<Tuple<T>>();
        result.add(new Tuple<>(subjectCreator.plain(), subjectCreator.plain(), null));
        result.add(new Tuple<>(subjectCreator.withAllFieldsChanged(), subjectCreator.withAllFieldsChanged(), null));
        return result;
    }

    private void checkPreconditions() {
        for (T example : equalExamples) {
            assertTrue(
                Formatter.of("Precondition:\n  %%\nand\n  %%\nare of different classes", equalExamples.get(0), example),
                type.isAssignableFrom(example.getClass()));
        }
    }

    private void checkEqualButNotIdentical(T reference, T other) {
        assertFalse(Formatter.of("Precondition: the same object appears twice:\n  %%", reference), reference == other);
        assertFalse(
            Formatter.of("Precondition: two identical objects appear:\n  %%", reference),
            isIdentical(reference, other));
        assertTrue(
            Formatter.of("Precondition: not all equal objects are equal:\n  %%\nand\n  %%", reference, other),
            reference.equals(other));
    }

    private void checkSingle(T reference, T copy) {
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
                reference);
        }
        catch (ClassCastException e) {
            Formatter f =
                    Formatter.of("""
                                 Generics: ClassCastException was thrown.
                                 Consider using withPrefabValuesForField, withGenericPrefabValues, or forExamples \
                                 for the type that triggered the exception.""");
            fail(f, e);
        }
    }

    private void checkNonNullity(T reference) {
        try {
            @SuppressWarnings("EqualsNull")
            boolean nullity = reference.equals(null);
            assertFalse(Formatter.of("Non-nullity: true returned for null value"), nullity);
        }
        catch (NullPointerException e) {
            fail(Formatter.of("Non-nullity: NullPointerException thrown"), e);
        }
    }

    private void checkTypeCheck(T reference) {
        SomethingElse somethingElse = new SomethingElse();
        try {
            Formatter f = Formatter.of("""
                                       Type-check: equals returns true for an unrelated type.
                                       Add an instanceof or getClass() check.""");
            assertFalse(f, reference.equals(somethingElse));
        }
        catch (AssertionException e) {
            throw e;
        }
        catch (ClassCastException e) {
            Formatter f = Formatter
                    .of("Type-check: equals throws ClassCastException.\nAdd an instanceof or getClass() check.");
            fail(f, e);
        }
        catch (Exception e) {
            Formatter f = Formatter
                    .of(
                        "Type-check: equals throws %%.\nAdd an instanceof or getClass() check.",
                        e.getClass().getSimpleName());
            fail(f, e);
        }
    }

    private void checkHashCode(T reference, T copy) {
        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        assertEquals(
            Formatter.of("hashCode: hashCode should be consistent:\n  %% (%%)", reference, referenceHashCode),
            referenceHashCode,
            cachedHashCodeInitializer.getInitializedHashCode(reference));

        if (!reference.equals(copy)) {
            return;
        }

        int copyHashCode = cachedHashCodeInitializer.getInitializedHashCode(copy);
        Formatter f = Formatter
                .of(
                    "hashCode: hashCodes should be equal:\n  %% (%%)\nand\n  %% (%%)",
                    reference,
                    referenceHashCode,
                    copy,
                    copyHashCode);
        assertEquals(f, referenceHashCode, copyHashCode);
    }

    private boolean isIdentical(T reference, T other) {
        return rethrow(() -> {
            for (FieldProbe probe : FieldIterable.of(reference.getClass())) {
                if (!Objects.equals(probe.getValue(reference), probe.getValue(other))) {
                    return false;
                }
            }

            return true;
        });
    }

    private static final class SomethingElse {

        @Override
        public int hashCode() {
            // Must return a stable, high-value hashCode,
            // otherwise the SymmetryTest (which depends on the hashCode) becomes flaky.
            return Integer.MAX_VALUE;
        }
    }
}
