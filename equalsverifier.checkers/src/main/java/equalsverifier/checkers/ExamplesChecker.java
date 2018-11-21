package equalsverifier.checkers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import equalsverifier.reflection.FieldIterable;
import equalsverifier.reflection.ObjectAccessor;
import equalsverifier.utils.CachedHashCodeInitializer;
import equalsverifier.utils.Configuration;
import equalsverifier.utils.Formatter;
import equalsverifier.utils.exceptions.AssertionException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static equalsverifier.utils.Assert.*;

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
            assertTrue(Formatter.of("Precondition:\n  %%\nand\n  %%\nare of different classes", equalExamples.get(0), example),
                    type.isAssignableFrom(example.getClass()));
        }
    }

    private void checkEqualButNotIdentical(T reference, T other) {
        assertFalse(Formatter.of("Precondition: the same object appears twice:\n  %%", reference),
                reference == other);
        assertFalse(Formatter.of("Precondition: two identical objects appear:\n  %%", reference),
                isIdentical(reference, other));
        assertTrue(Formatter.of("Precondition: not all equal objects are equal:\n  %%\nand\n  %%", reference, other),
                reference.equals(other));
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
            assertEquals(Formatter.of("Reflexivity: object does not equal itself:\n  %%", reference),
                reference, reference);
        }
        catch (ClassCastException e) {
            fail(Formatter.of(
                "Generics: ClassCastException was thrown. Consider using withGenericPrefabValues for the type that triggered the exception."),
                e);
        }
    }

    @SuppressFBWarnings(value = "EC_NULL_ARG", justification = "Check what happens when null is passed into equals.")
    private void checkNonNullity(T reference) {
        try {
            boolean nullity = reference.equals(null);
            assertFalse(Formatter.of("Non-nullity: true returned for null value"), nullity);
        }
        catch (NullPointerException e) {
            fail(Formatter.of("Non-nullity: NullPointerException thrown"), e);
        }
    }

    private void checkTypeCheck(T reference) {
        class SomethingElse {}
        SomethingElse somethingElse = new SomethingElse();
        try {
            assertFalse(Formatter.of("Type-check: equals returns true for an unrelated type.\nAdd an instanceof or getClass() check."),
                    reference.equals(somethingElse));
        }
        catch (AssertionException e) {
            throw e;
        }
        catch (ClassCastException e) {
            fail(Formatter.of("Type-check: equals throws ClassCastException.\nAdd an instanceof or getClass() check."), e);
        }
        catch (Exception e) {
            fail(Formatter.of("Type-check: equals throws %%.\nAdd an instanceof or getClass() check.", e.getClass().getSimpleName()), e);
        }
    }

    private void checkHashCode(T reference, T copy) {
        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        assertEquals(Formatter.of("hashCode: hashCode should be consistent:\n  %% (%%)", reference, referenceHashCode),
                referenceHashCode, cachedHashCodeInitializer.getInitializedHashCode(reference));

        if (!reference.equals(copy)) {
            return;
        }

        int copyHashCode = cachedHashCodeInitializer.getInitializedHashCode(copy);
        Formatter f = Formatter.of("hashCode: hashCodes should be equal:\n  %% (%%)\nand\n  %% (%%)",
                reference, referenceHashCode, copy, copyHashCode);
        assertEquals(f, referenceHashCode, copyHashCode);
    }

    private boolean isIdentical(T reference, T other) {
        for (Field field : FieldIterable.of(reference.getClass())) {
            try {
                field.setAccessible(true);
                if (!Objects.equals(field.get(reference), field.get(other))) {
                    return false;
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                return false;
            }
        }

        return true;
    }
}
