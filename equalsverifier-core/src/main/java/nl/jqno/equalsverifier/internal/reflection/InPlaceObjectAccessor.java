package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Implementation of ObjectAccessor that modifies its wrapped object in-place through reflection.
 *
 * @param <T> The specified object's class.
 */
final class InPlaceObjectAccessor<T> extends ObjectAccessor<T> {

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */InPlaceObjectAccessor(T object, Class<T> type) {
        super(object, type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        T copy = Instantiator.of(type()).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        S copy = Instantiator.of(subclass).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public T copyIntoAnonymousSubclass() {
        T copy = Instantiator.of(type()).instantiateAnonymousSubclass();
        return copyInto(copy);
    }

    private <S> S copyInto(S copy) {
        for (Field field : FieldIterable.of(type())) {
            fieldModifierFor(field).copyTo(copy);
        }
        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(PrefabValues prefabValues, TypeTag enclosingType) {
        return scramble(prefabValues, enclosingType, new LinkedHashSet<>());
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(
        PrefabValues prefabValues,
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return scrambleInternal(prefabValues, enclosingType, typeStack, FieldIterable::of);
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> shallowScramble(PrefabValues prefabValues, TypeTag enclosingType) {
        return scrambleInternal(
            prefabValues,
            enclosingType,
            new LinkedHashSet<>(),
            FieldIterable::ofIgnoringSuper
        );
    }

    private ObjectAccessor<T> scrambleInternal(
        PrefabValues prefabValues,
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack,
        Function<Class<?>, FieldIterable> it
    ) {
        for (Field field : it.apply(type())) {
            try {
                fieldModifierFor(field).changeField(prefabValues, enclosingType, typeStack);
            } catch (ModuleException e) {
                handleInaccessibleObjectException(e.getCause(), field);
            } catch (RuntimeException e) {
                // InaccessibleObjectException is not yet available in Java 8
                if (e.getClass().getName().endsWith("InaccessibleObjectException")) {
                    handleInaccessibleObjectException(e, field);
                } else {
                    throw e;
                }
            }
        }
        return this;
    }

    private void handleInaccessibleObjectException(Throwable e, Field field) {
        if (e.getMessage() != null && e.getMessage().contains(type().getCanonicalName())) {
            throw new ModuleException(
                "The class is not accessible via the Java Module system. Consider opening the module that contains it.",
                e
            );
        } else {
            throw new ModuleException(
                "Field " +
                field.getName() +
                " of type " +
                field.getType().getName() +
                " is not accessible via the Java Module System.\nConsider opening the module that contains it, or add prefab values for type " +
                field.getType().getName() +
                ".",
                e
            );
        }
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> clear(
        Predicate<Field> canBeDefault,
        PrefabValues prefabValues,
        TypeTag enclosingType
    ) {
        for (Field field : FieldIterable.of(type())) {
            FieldModifier modifier = fieldModifierFor(field);
            modifier.defaultField();
            if (!canBeDefault.test(field)) {
                modifier.changeField(prefabValues, enclosingType);
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withDefaultedField(Field field) {
        fieldModifierFor(field).defaultField();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withChangedField(
        Field field,
        PrefabValues prefabValues,
        TypeTag enclosingType
    ) {
        fieldModifierFor(field).changeField(prefabValues, enclosingType);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> withFieldSetTo(Field field, Object newValue) {
        fieldModifierFor(field).set(newValue);
        return this;
    }

    private FieldModifier fieldModifierFor(Field field) {
        return FieldModifier.of(field, get());
    }
}
