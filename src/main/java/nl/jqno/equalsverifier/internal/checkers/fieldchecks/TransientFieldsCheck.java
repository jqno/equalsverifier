package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class TransientFieldsCheck<T> implements FieldCheck<T> {
    private final PrefabValues prefabValues;
    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;

    public TransientFieldsCheck(Configuration<T> config) {
        this.prefabValues = config.getPrefabValues();
        this.typeTag = config.getTypeTag();
        this.annotationCache = config.getAnnotationCache();
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor, ObjectAccessor<T> copyAccessor, Field field) {
        T reference = referenceAccessor.get();
        T changed = copyAccessor.withChangedField(field, prefabValues, typeTag).get();

        boolean equalsChanged = !reference.equals(changed);
        boolean hasAnnotation =
                annotationCache.hasFieldAnnotation(
                        typeTag.getType(), field.getName(), SupportedAnnotations.TRANSIENT);
        boolean fieldIsTransient =
                referenceAccessor.fieldAccessorFor(field).fieldIsTransient() || hasAnnotation;
        if (equalsChanged && fieldIsTransient) {
            fail(
                    Formatter.of(
                            "Transient field %% should not be included in equals/hashCode contract.",
                            field.getName()));
        }

        referenceAccessor.withChangedField(field, prefabValues, typeTag);
    }
}
