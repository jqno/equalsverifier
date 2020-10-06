package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class SymmetryFieldCheck<T> implements FieldCheck<T> {
    private final PrefabValues prefabValues;
    private final TypeTag typeTag;

    public SymmetryFieldCheck(PrefabValues prefabValues, TypeTag typeTag) {
        this.prefabValues = prefabValues;
        this.typeTag = typeTag;
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor,
            ObjectAccessor<T> copyAccessor,
            FieldAccessor fieldAccessor) {
        Field field = fieldAccessor.getField();

        checkSymmetry(referenceAccessor, copyAccessor);

        ObjectAccessor<T> changedAccessor =
                copyAccessor.withChangedField(field, prefabValues, typeTag);
        checkSymmetry(referenceAccessor, changedAccessor);

        ObjectAccessor<T> changedReferenceAccessor =
                referenceAccessor.withChangedField(field, prefabValues, typeTag);
        checkSymmetry(changedReferenceAccessor, changedAccessor);
    }

    private void checkSymmetry(
            ObjectAccessor<T> referenceAccessor, ObjectAccessor<T> copyAccessor) {
        T left = referenceAccessor.get();
        T right = copyAccessor.get();
        assertTrue(
                Formatter.of("Symmetry: objects are not symmetric:\n  %%\nand\n  %%", left, right),
                left.equals(right) == right.equals(left));
    }
}
