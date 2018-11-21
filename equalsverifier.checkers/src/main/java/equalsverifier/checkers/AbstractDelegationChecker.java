package equalsverifier.checkers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.reflection.ClassAccessor;
import equalsverifier.reflection.FieldIterable;
import equalsverifier.utils.CachedHashCodeInitializer;
import equalsverifier.utils.Configuration;
import equalsverifier.utils.Formatter;

import java.lang.reflect.Field;

import static equalsverifier.utils.Assert.fail;

public class AbstractDelegationChecker<T> implements Checker {
    private final Class<T> type;
    private final TypeTag typeTag;
    private final PrefabAbstract prefabAbstract;
    private final ClassAccessor<T> classAccessor;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public AbstractDelegationChecker(Configuration<T> config) {
        this.type = config.getType();
        this.typeTag = config.getTypeTag();
        this.prefabAbstract = config.getPrefabValues();
        this.classAccessor = config.getClassAccessor();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkAbstractEqualsAndHashCode();

        checkAbstractDelegationInFields();

        T instance = prefabAbstract.giveRed(typeTag);
        T copy = prefabAbstract.giveBlack(typeTag);
        checkAbstractDelegation(instance, copy);
    }

    private void checkAbstractEqualsAndHashCode() {
        boolean equalsIsAbstract = classAccessor.isEqualsAbstract();
        boolean hashCodeIsAbstract = classAccessor.isHashCodeAbstract();

        if (equalsIsAbstract && hashCodeIsAbstract) {
            fail(Formatter.of("Abstract delegation: %%'s equals and hashCode methods are both abstract. They should be concrete.",
                    type.getSimpleName()));
        }
        else if (equalsIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, true, true));
        }
        else if (hashCodeIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, false, true));
        }
    }

    private void checkAbstractDelegationInFields() {
        for (Field field : FieldIterable.of(type)) {
            TypeTag tag = TypeTag.of(field, typeTag);
            Tuple<?> tuple = safelyGetTuple(tag);
            if (tuple != null) {
                Object instance = tuple.getRed();
                Object copy = tuple.getBlack();
                checkAbstractMethods(tag.getType(), instance, copy, true);
            }
        }
    }

    private <U> Tuple<U> safelyGetTuple(TypeTag tag) {
        try {
            return prefabAbstract.giveTuple(tag);
        }
        catch (Exception ignored) {
            // If it fails for some reason, any reason, just return null so we can skip the test.
            return null;
        }
    }

    private void checkAbstractDelegation(T instance, T copy) {
        checkAbstractMethods(type, instance, copy, false);
    }

    private Formatter buildSingleAbstractMethodErrorMessage(Class<?> c, boolean isEqualsAbstract, boolean bothShouldBeConcrete) {
        return Formatter.of("Abstract delegation: %%'s %% method is abstract, but %% is not.\n%%",
                c.getSimpleName(),
                isEqualsAbstract ? "equals" : "hashCode",
                isEqualsAbstract ? "hashCode" : "equals",
                bothShouldBeConcrete ? "Both should be concrete." : "Both should be either abstract or concrete.");
    }

    @SuppressFBWarnings(value = "DE_MIGHT_IGNORE", justification = "These exceptions will re-occur and be handled later.")
    private <S> void checkAbstractMethods(Class<?> instanceClass, S instance, S copy, boolean prefabPossible) {
        try {
            instance.equals(copy);
        }
        catch (AbstractMethodError e) {
            fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "equals", e.getMessage()), e);
        }
        catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }

        try {
            cachedHashCodeInitializer.getInitializedHashCode(instance);
        }
        catch (AbstractMethodError e) {
            fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "hashCode", e.getMessage()), e);
        }
        catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }
    }

    private Formatter buildAbstractDelegationErrorMessage(Class<?> c, boolean prefabPossible, String method, String originalMessage) {
        Formatter prefabFormatter = Formatter.of("\nAdd prefab values for %%.", c.getName());

        return Formatter.of("Abstract delegation: %%'s %% method delegates to an abstract method:\n %%%%",
                c.getSimpleName(), method, originalMessage, prefabPossible ? prefabFormatter.format() : "");
    }
}
