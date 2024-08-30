package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static net.bytebuddy.implementation.ExceptionMethod.throwing;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.instantiation.InstanceCreator;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class JpaLazyGetterFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final InstanceCreator instanceCreator;
    private final Class<T> type;
    private final ClassAccessor<T> accessor;
    private final AnnotationCache annotationCache;
    private final Function<String, String> fieldnameToGetter;
    private final boolean strictHashcode;

    public JpaLazyGetterFieldCheck(SubjectCreator<T> subjectCreator, Configuration<T> config) {
        this.subjectCreator = subjectCreator;
        this.instanceCreator = config.getInstanceCreator();
        this.type = config.getType();
        this.accessor = config.getClassAccessor();
        this.annotationCache = config.getAnnotationCache();
        this.fieldnameToGetter = config.getFieldnameToGetter();
        this.strictHashcode = config.getWarningsToSuppress().contains(Warning.STRICT_HASHCODE);
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        String fieldName = fieldProbe.getName();
        String getterName = fieldnameToGetter.apply(fieldName);

        if (
            !fieldIsUsed(fieldProbe.getField(), true) ||
            !fieldIsLazy(fieldName) ||
            Modifier.isFinal(type.getModifiers())
        ) {
            return;
        }

        assertEntity(fieldName, "equals", getterName, accessor.hasMethod(getterName));

        Class<T> sub = throwingGetterCreator(getterName);
        T red1 = instanceCreator.<T>instantiate(new TypeTag(sub)).getRed();
        T red2 = ObjectAccessor.of(red1, sub).copy();

        boolean equalsExceptionCaught = false;
        try {
            red1.equals(red2);
        } catch (EqualsVerifierInternalBugException e) {
            equalsExceptionCaught = true;
        }
        assertEntity(fieldName, "equals", getterName, equalsExceptionCaught);

        boolean usedInHashcode = !strictHashcode || fieldIsUsed(fieldProbe.getField(), false);
        boolean hashCodeExceptionCaught = false;
        try {
            red1.hashCode();
        } catch (EqualsVerifierInternalBugException e) {
            hashCodeExceptionCaught = true;
        }
        assertEntity(fieldName, "hashCode", getterName, hashCodeExceptionCaught || !usedInHashcode);
    }

    private boolean fieldIsUsed(Field field, boolean forEquals) {
        T red = subjectCreator.plain();
        T blue = subjectCreator.withFieldChanged(field);

        if (forEquals) {
            return !red.equals(blue);
        } else {
            return red.hashCode() != blue.hashCode();
        }
    }

    private boolean fieldIsLazy(String fieldName) {
        return (
            annotationCache.hasFieldAnnotation(
                type,
                fieldName,
                SupportedAnnotations.JPA_LINKED_FIELD
            ) ||
            annotationCache.hasFieldAnnotation(type, fieldName, SupportedAnnotations.JPA_LAZY_FIELD)
        );
    }

    private Class<T> throwingGetterCreator(String getterName) {
        return Instantiator.giveDynamicSubclass(
            type,
            getterName,
            builder ->
                builder
                    .method(named(getterName))
                    .intercept(throwing(EqualsVerifierInternalBugException.class))
        );
    }

    private void assertEntity(
        String fieldName,
        String method,
        String getterName,
        boolean assertion
    ) {
        assertTrue(
            Formatter.of(
                "JPA Entity: direct reference to field %% used in %% instead of getter %%().",
                fieldName,
                method,
                getterName
            ),
            assertion
        );
    }
}
