package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class TransientFieldsCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "A cache is inherently mutable.")
    public TransientFieldsCheck(
        SubjectCreator<T> subjectCreator,
        TypeTag typeTag,
        AnnotationCache annotationCache
    ) {
        this.subjectCreator = subjectCreator;
        this.typeTag = typeTag;
        this.annotationCache = annotationCache;
    }

    @Override
    public void execute(Field changedField) {
        T reference = subjectCreator.plain();
        T changed = subjectCreator.withFieldChanged(changedField);

        boolean equalsChanged = !reference.equals(changed);
        boolean hasAnnotation = annotationCache.hasFieldAnnotation(
            typeTag.getType(),
            changedField.getName(),
            SupportedAnnotations.TRANSIENT
        );
        boolean fieldIsTransient =
            FieldAccessor.of(changedField).fieldIsTransient() || hasAnnotation;
        if (equalsChanged && fieldIsTransient) {
            fail(
                Formatter.of(
                    "Transient field %% should not be included in equals/hashCode contract.",
                    changedField.getName()
                )
            );
        }
    }
}
