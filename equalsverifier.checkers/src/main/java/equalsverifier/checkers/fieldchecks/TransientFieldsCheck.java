package equalsverifier.checkers.fieldchecks;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.reflection.FieldAccessor;
import equalsverifier.reflection.annotations.AnnotationCache;
import equalsverifier.reflection.annotations.SupportedAnnotations;
import equalsverifier.utils.Configuration;
import equalsverifier.utils.Formatter;

import static equalsverifier.utils.Assert.fail;

public class TransientFieldsCheck<T> implements FieldCheck {
    private final PrefabAbstract prefabAbstract;
    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;

    public TransientFieldsCheck(Configuration<T> config) {
        this.prefabAbstract = config.getPrefabValues();
        this.typeTag = config.getTypeTag();
        this.annotationCache = config.getAnnotationCache();
    }

    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Object reference = referenceAccessor.getObject();
        Object changed = changedAccessor.getObject();

        changedAccessor.changeField(prefabAbstract, typeTag);

        boolean equalsChanged = !reference.equals(changed);
        boolean fieldIsTransient = referenceAccessor.fieldIsTransient() ||
                annotationCache.hasFieldAnnotation(typeTag.getType(), referenceAccessor.getFieldName(), SupportedAnnotations.TRANSIENT);

        if (equalsChanged && fieldIsTransient) {
            fail(Formatter.of("Transient field %% should not be included in equals/hashCode contract.", referenceAccessor.getFieldName()));
        }

        referenceAccessor.changeField(prefabAbstract, typeTag);
    }
}
