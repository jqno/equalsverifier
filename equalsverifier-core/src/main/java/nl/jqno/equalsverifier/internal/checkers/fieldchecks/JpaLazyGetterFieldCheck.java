package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

// import static net.bytebuddy.implementation.ExceptionMethod.throwing;
// import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.implementation.ExceptionMethod.throwing;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class JpaLazyGetterFieldCheck<T> implements FieldCheck<T> {

    private final Class<T> type;
    private final ClassAccessor<T> accessor;
    private final PrefabValues prefabValues;
    private final AnnotationCache annotationCache;

    public JpaLazyGetterFieldCheck(Configuration<T> config) {
        this.type = config.getType();
        this.accessor = config.getClassAccessor();
        this.prefabValues = config.getPrefabValues();
        this.annotationCache = config.getAnnotationCache();
    }

    @Override
    public void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        String fieldName = fieldAccessor.getFieldName();
        String getterName =
            "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        if (!fieldIsLazy(fieldAccessor)) {
            return;
        }

        assertEntity(fieldName, getterName, accessor.declaresMethod(getterName));
        ClassAccessor<T> subAccessor = throwingGetterAccessor(getterName);

        T red = subAccessor.getRedObject(TypeTag.NULL);
        T blue = subAccessor.getBlueObject(TypeTag.NULL);

        boolean exceptionCaught = false;
        try {
            red.equals(blue);
        } catch (EqualsVerifierInternalBugException e) {
            exceptionCaught = true;
        }

        assertEntity(fieldName, getterName, exceptionCaught);
    }

    private boolean fieldIsLazy(FieldAccessor fieldAccessor) {
        return annotationCache.hasFieldAnnotation(
            type,
            fieldAccessor.getFieldName(),
            SupportedAnnotations.LAZY_FIELD
        );
    }

    private ClassAccessor<T> throwingGetterAccessor(String getterName) {
        Class<T> sub = Instantiator.giveDynamicSubclass(
            type,
            getterName,
            builder ->
                builder
                    .method(named(getterName))
                    .intercept(throwing(EqualsVerifierInternalBugException.class))
        );
        return ClassAccessor.of(sub, prefabValues);
    }

    private void assertEntity(String fieldName, String getterName, boolean assertion) {
        assertTrue(
            Formatter.of(
                "JPA Entity: direct reference to field %% used in equals instead of getter %%.",
                fieldName,
                getterName
            ),
            assertion
        );
    }
}
