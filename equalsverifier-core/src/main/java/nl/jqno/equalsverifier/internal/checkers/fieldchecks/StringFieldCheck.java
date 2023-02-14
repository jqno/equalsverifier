package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class StringFieldCheck<T> implements FieldCheck<T> {

    public static final String ERROR_DOC_TITLE = "String equality";

    private final PrefabValues prefabValues;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public StringFieldCheck(
        PrefabValues prefabValues,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer
    ) {
        this.prefabValues = prefabValues;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_CONVERT_CASE",
        justification = "String prefab values are probably not localized."
    )
    public void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        if (String.class.equals(fieldAccessor.getFieldType()) && !fieldAccessor.fieldIsStatic()) {
            Field field = fieldAccessor.getField();
            String red = prefabValues.giveRed(new TypeTag(String.class));

            final T reference;
            final T copy;
            try {
                reference = referenceAccessor.withFieldSetTo(field, red.toLowerCase()).get();
                copy = copyAccessor.withFieldSetTo(field, red.toUpperCase()).get();
            } catch (ReflectionException ignored) {
                // Differently-cased String is not allowed, so cannot cause problems either.
                return;
            }

            boolean theyAreEqual = reference.equals(copy);
            boolean theirHashCodesAreEqual =
                cachedHashCodeInitializer.getInitializedHashCode(reference) ==
                cachedHashCodeInitializer.getInitializedHashCode(copy);

            if (theyAreEqual && !theirHashCodesAreEqual) {
                fail(
                    Formatter.of(
                        ERROR_DOC_TITLE +
                        ": class uses equalsIgnoreCase to compare String field %%, but hashCode is case-sensitive." +
                        " Use toUpperCase() to determine the hashCode.",
                        field.getName()
                    )
                );
            }
        }
    }
}
