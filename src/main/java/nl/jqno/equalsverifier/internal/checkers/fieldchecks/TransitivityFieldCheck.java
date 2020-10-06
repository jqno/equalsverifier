package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class TransitivityFieldCheck<T> implements FieldCheck<T> {
    private final PrefabValues prefabValues;
    private final TypeTag typeTag;

    public TransitivityFieldCheck(PrefabValues prefabValues, TypeTag typeTag) {
        this.prefabValues = prefabValues;
        this.typeTag = typeTag;
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor,
            ObjectAccessor<T> copyAccessor,
            FieldAccessor fieldAccessor) {
        Field field = fieldAccessor.getField();
        T a1 = referenceAccessor.get();
        T b1 = buildB1(copyAccessor, field);
        T b2 = buildB2(referenceAccessor, field);

        boolean x = a1.equals(b1);
        boolean y = b1.equals(b2);
        boolean z = a1.equals(b2);

        if (countFalses(x, y, z) == 1) {
            fail(
                    Formatter.of(
                            "Transitivity: two of these three instances are equal to each other,"
                                    + " so the third one should be, too:\n-  %%\n-  %%\n-  %%",
                            a1, b1, b2));
        }
    }

    private T buildB1(ObjectAccessor<T> accessor, Field field) {
        accessor.withChangedField(field, prefabValues, typeTag);
        return accessor.get();
    }

    private T buildB2(ObjectAccessor<T> referenceAccessor, Field field) {
        T copy = referenceAccessor.copy();
        ObjectAccessor<T> objectAccessor = ObjectAccessor.of(copy);
        objectAccessor = objectAccessor.withChangedField(field, prefabValues, typeTag);
        for (Field f : FieldIterable.of(referenceAccessor.type())) {
            if (!f.equals(field)) {
                objectAccessor = objectAccessor.withChangedField(f, prefabValues, typeTag);
            }
        }
        return objectAccessor.get();
    }

    private int countFalses(boolean... bools) {
        int result = 0;
        for (boolean b : bools) {
            if (!b) {
                result++;
            }
        }
        return result;
    }
}
