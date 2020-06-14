package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MutableStateFieldCheck implements FieldCheck {
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
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        if (isCachedHashCodeField.test(referenceAccessor)) {
            return;
        }

        Object reference = referenceAccessor.getObject();
        Object changed = changedAccessor.getObject();

        boolean equalBefore = reference.equals(changed);
        changedAccessor.changeField(prefabValues, typeTag);
        boolean equalAfter = reference.equals(changed);

        if (equalBefore && !equalAfter && !referenceAccessor.fieldIsFinal()) {
            String message =
                    "Mutability: equals depends on mutable field %%.\n"
                            + "Make the field final, suppress Warning.NONFINAL_FIELDS or use EqualsVerifier.simple()";
            fail(Formatter.of(message, referenceAccessor.getFieldName()));
        }

        referenceAccessor.changeField(prefabValues, typeTag);
    }
}
