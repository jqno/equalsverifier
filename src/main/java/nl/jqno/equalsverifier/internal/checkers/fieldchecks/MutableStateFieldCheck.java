package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MutableStateFieldCheck<T> implements FieldCheck<T> {
    private final PrefabValues prefabValues;
    private final TypeTag typeTag;
    private final Predicate<FieldAccessor> isCachedHashCodeField;

    public MutableStateFieldCheck(
            PrefabValues prefabValues,
            TypeTag typeTag,
            Predicate<FieldAccessor> isCachedHashCodeField) {
        this.prefabValues = prefabValues;
        this.typeTag = typeTag;
        this.isCachedHashCodeField = isCachedHashCodeField;
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor, ObjectAccessor<T> copyAccessor, Field field) {
        FieldAccessor fieldAccessor = referenceAccessor.fieldAccessorFor(field);
        if (isCachedHashCodeField.test(fieldAccessor)) {
            return;
        }

        T reference = referenceAccessor.get();
        T copy = copyAccessor.get();

        boolean equalBefore = reference.equals(copy);
        T changed = copyAccessor.withChangedField(field, prefabValues, typeTag).get();
        boolean equalAfter = reference.equals(changed);

        if (equalBefore && !equalAfter && !fieldAccessor.fieldIsFinal()) {
            String message =
                    "Mutability: equals depends on mutable field %%.\n"
                            + "Make the field final, suppress Warning.NONFINAL_FIELDS or use"
                            + " EqualsVerifier.simple()";
            fail(Formatter.of(message, field.getName()));
        }

        referenceAccessor.withChangedField(field, prefabValues, typeTag);
    }
}
