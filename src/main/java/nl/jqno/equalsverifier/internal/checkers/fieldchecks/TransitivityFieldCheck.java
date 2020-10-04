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
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Object a1 = referenceAccessor.getObject();
        Object b1 = buildB1(changedAccessor);
        Object b2 = buildB2(a1, referenceAccessor.getField());

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

    private Object buildB1(FieldAccessor accessor) {
        accessor.changeField(prefabValues, typeTag);
        return accessor.getObject();
    }

    private Object buildB2(Object a1, Field referenceField) {
        Object result = ObjectAccessor.of(a1).copy();
        ObjectAccessor<?> objectAccessor = ObjectAccessor.of(result);
        objectAccessor.fieldAccessorFor(referenceField).changeField(prefabValues, typeTag);
        for (Field field : FieldIterable.of(result.getClass())) {
            if (!field.equals(referenceField)) {
                objectAccessor.fieldAccessorFor(field).changeField(prefabValues, typeTag);
            }
        }
        return result;
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
