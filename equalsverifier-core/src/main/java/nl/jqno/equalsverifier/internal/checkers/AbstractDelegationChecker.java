package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.util.*;

public class AbstractDelegationChecker<T> implements Checker {

    private final Class<T> type;
    private final TypeTag typeTag;
    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;
    private final ClassProbe<T> classProbe;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public AbstractDelegationChecker(Context<T> context) {
        Configuration<T> config = context.getConfiguration();
        this.type = context.getType();
        this.typeTag = config.getTypeTag();
        this.subjectCreator = context.getSubjectCreator();
        this.valueProvider = context.getValueProvider();
        this.classProbe = context.getClassProbe();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkAbstractEqualsAndHashCode();

        checkAbstractDelegationInFields();

        T instance = subjectCreator.plain();
        T copy = subjectCreator.plain();
        checkAbstractDelegation(instance, copy);
    }

    private void checkAbstractEqualsAndHashCode() {
        boolean equalsIsAbstract = classProbe.isEqualsAbstract();
        boolean hashCodeIsAbstract = classProbe.isHashCodeAbstract();

        if (equalsIsAbstract && hashCodeIsAbstract) {
            fail(
                Formatter.of(
                    "Abstract delegation: %%'s equals and hashCode methods are both abstract. They should be concrete.",
                    type.getSimpleName()
                )
            );
        } else if (equalsIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, true, true));
        } else if (hashCodeIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, false, true));
        }
    }

    private void checkAbstractDelegationInFields() {
        for (Field field : FieldIterable.of(type)) {
            TypeTag tag = TypeTag.of(field, typeTag);
            Tuple<?> tuple = safelyGetTuple(tag);
            if (tuple != null) {
                Object instance = tuple.getRed();
                Object copy = tuple.getBlue();
                checkAbstractMethods(tag.getType(), instance, copy, true);
            }
        }
    }

    private <U> Tuple<U> safelyGetTuple(TypeTag tag) {
        try {
            return valueProvider.provide(tag);
        } catch (Exception ignored) {
            // If it fails for some reason, any reason, just return null so we can skip the test.
            return null;
        }
    }

    private void checkAbstractDelegation(T instance, T copy) {
        checkAbstractMethods(type, instance, copy, false);
    }

    private Formatter buildSingleAbstractMethodErrorMessage(
        Class<?> c,
        boolean isEqualsAbstract,
        boolean bothShouldBeConcrete
    ) {
        return Formatter.of(
            "Abstract delegation: %%'s %% method is abstract, but %% is not.\n%%",
            c.getSimpleName(),
            isEqualsAbstract ? "equals" : "hashCode",
            isEqualsAbstract ? "hashCode" : "equals",
            bothShouldBeConcrete
                ? "Both should be concrete."
                : "Both should be either abstract or concrete."
        );
    }

    @SuppressFBWarnings(
        value = "DE_MIGHT_IGNORE",
        justification = "These exceptions will re-occur and be handled later."
    )
    private <S> void checkAbstractMethods(
        Class<?> instanceClass,
        S instance,
        S copy,
        boolean prefabPossible
    ) {
        try {
            instance.equals(copy);
        } catch (AbstractMethodError e) {
            Formatter f = buildAbstractDelegationErrorMessage(
                instanceClass,
                prefabPossible,
                "equals",
                e.getMessage()
            );
            fail(f, e);
        } catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }

        try {
            cachedHashCodeInitializer.getInitializedHashCode(instance);
        } catch (AbstractMethodError e) {
            Formatter f = buildAbstractDelegationErrorMessage(
                instanceClass,
                prefabPossible,
                "hashCode",
                e.getMessage()
            );
            fail(f, e);
        } catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }
    }

    private Formatter buildAbstractDelegationErrorMessage(
        Class<?> c,
        boolean prefabPossible,
        String method,
        String originalMessage
    ) {
        Formatter prefabFormatter = Formatter.of("\nAdd prefab values for %%.", c.getName());

        return Formatter.of(
            "Abstract delegation: %%'s %% method delegates to an abstract method:\n %%%%",
            c.getSimpleName(),
            method,
            originalMessage,
            prefabPossible ? prefabFormatter.format() : ""
        );
    }
}
