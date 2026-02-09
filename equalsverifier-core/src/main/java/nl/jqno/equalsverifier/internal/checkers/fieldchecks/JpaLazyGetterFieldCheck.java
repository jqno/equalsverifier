package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static net.bytebuddy.implementation.ExceptionMethod.throwing;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class JpaLazyGetterFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final Class<T> type;
    private final ClassProbe<T> classProbe;
    private final AnnotationCache annotationCache;
    private final Function<String, String> fieldnameToGetter;
    private final boolean strictHashcode;

    public JpaLazyGetterFieldCheck(Context<T> context) {
        this.subjectCreator = context.getSubjectCreator();
        this.type = context.getType();
        this.classProbe = context.getClassProbe();

        Configuration<T> config = context.getConfiguration();
        this.annotationCache = config.annotationCache();
        this.fieldnameToGetter = config.fieldnameToGetter();
        this.strictHashcode = config.warningsToSuppress().contains(Warning.STRICT_HASHCODE);
    }

    @Override
    @SuppressWarnings("ReturnValueIgnored")
    public void execute(FieldProbe fieldProbe) {
        String fieldName = fieldProbe.getName();
        String fieldDisplayName = fieldProbe.getDisplayName();
        String getterName = fieldnameToGetter.apply(fieldName);

        if (!fieldIsUsed(fieldProbe.getField(), true)
                || !fieldIsLazy(fieldName)
                || Modifier.isFinal(type.getModifiers())) {
            return;
        }

        assertTrue(
            Formatter
                    .of(
                        "Class %% doesn't contain getter %%() for field %%.",
                        classProbe.getType().getSimpleName(),
                        getterName,
                        fieldDisplayName),
            classProbe.hasMethod(getterName));
        assertFalse(
            Formatter
                    .of(
                        """
                        Getter method %% in JPA entity class %% is final.
                           EqualsVerifier cannot determine if %% calls getters instead of referencing field %% directly.
                           Please make the method non-final, or suppress Warning.JPA_GETTER to disable the check.""",
                        getterName,
                        classProbe.getType().getSimpleName(),
                        classProbe.getType().getSimpleName(),
                        fieldDisplayName),
            classProbe.isMethodFinal(getterName));

        Class<? extends T> sub = throwingGetterCreator(getterName);
        T original = subjectCreator.plain();
        T red1 = subjectCreator.copyIntoSubclass(original, sub, null, "withFactory");
        T red2 = subjectCreator.copyIntoSubclass(original, sub, null, "withFactory");

        boolean equalsExceptionCaught = false;
        try {
            red1.equals(red2);
        }
        catch (EqualsVerifierInternalBugException e) {
            equalsExceptionCaught = true;
        }
        assertEntity(fieldDisplayName, "equals", getterName, equalsExceptionCaught);

        boolean usedInHashcode = !strictHashcode || fieldIsUsed(fieldProbe.getField(), false);
        boolean hashCodeExceptionCaught = false;
        try {
            red1.hashCode();
        }
        catch (EqualsVerifierInternalBugException e) {
            hashCodeExceptionCaught = true;
        }
        assertEntity(fieldDisplayName, "hashCode", getterName, hashCodeExceptionCaught || !usedInHashcode);
    }

    private boolean fieldIsUsed(Field field, boolean forEquals) {
        T red = subjectCreator.plain();
        T blue = subjectCreator.withFieldChanged(field);

        if (forEquals) {
            return !red.equals(blue);
        }
        else {
            return red.hashCode() != blue.hashCode();
        }
    }

    private boolean fieldIsLazy(String fieldName) {
        return annotationCache.hasFieldAnnotation(type, fieldName, SupportedAnnotations.JPA_LINKED_FIELD)
                || annotationCache.hasFieldAnnotation(type, fieldName, SupportedAnnotations.JPA_LAZY_FIELD);
    }

    private Class<T> throwingGetterCreator(String getterName) {
        return SubtypeManager
                .giveDynamicSubclass(
                    type,
                    getterName,
                    builder -> builder
                            .method(named(getterName))
                            .intercept(throwing(EqualsVerifierInternalBugException.class)));
    }

    private void assertEntity(String fieldDisplayName, String method, String getterName, boolean assertion) {
        assertTrue(
            Formatter
                    .of(
                        "JPA Entity: direct reference to field %% used in %% instead of getter %%().",
                        fieldDisplayName,
                        method,
                        getterName),
            assertion);
    }
}
