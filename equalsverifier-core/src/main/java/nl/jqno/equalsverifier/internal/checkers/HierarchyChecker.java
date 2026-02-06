package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Predicate;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.SubtypeManager;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.*;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class HierarchyChecker<T> implements Checker {

    private final Configuration<T> config;
    private final Class<T> type;
    private final SubjectCreator<T> subjectCreator;
    private final ClassProbe<T> classProbe;
    private final boolean strictnessSuppressed;
    private final boolean versionedEntity;
    private final boolean hasRedefinedSubclass;
    private final boolean typeIsFinal;
    private final boolean typeIsSealed;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public HierarchyChecker(Context<T> context) {
        this.config = context.getConfiguration();

        this.strictnessSuppressed = config.warningsToSuppress().contains(Warning.STRICT_INHERITANCE);
        this.versionedEntity = config.warningsToSuppress().contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);
        this.hasRedefinedSubclass = config.redefinedSubclass() != null || config.redefinedSubclassFactory() != null;
        if (strictnessSuppressed && hasRedefinedSubclass) {
            fail(Formatter.of("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive."));
        }

        this.type = context.getType();
        this.subjectCreator = context.getSubjectCreator();
        this.classProbe = context.getClassProbe();
        this.typeIsFinal = Modifier.isFinal(type.getModifiers());
        this.typeIsSealed = classProbe.isSealed();
        this.cachedHashCodeInitializer = config.cachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkSuperclass();
        checkSubclass();

        checkRedefinedSubclass();
        checkFinalEqualsMethod();
    }

    private void checkSuperclass() {
        ClassProbe<? super T> superProbe = classProbe.getSuperProbe();
        if (superProbe.isEqualsInheritedFromObject() || superProbe.isSealed()) {
            return;
        }

        if (config.hasRedefinedSuperclass() || config.usingGetClass()) {
            T reference = subjectCreator.plain();
            Object equalSuper = getEqualSuper(reference);
            checkMatchingValues(equalSuper, reference, "superclass");

            Formatter formatter = Formatter
                    .of(
                        "Redefined superclass:\n  %%\nshould not equal superclass instance\n  %%\nbut it does.",
                        reference,
                        equalSuper);
            try {
                assertFalse(formatter, reference.equals(equalSuper) || equalSuper.equals(reference));
            }
            catch (AbstractMethodError ignored) {
                // In this case, we'll assume all super properties hold.
                // The problems we test for, can never occur anyway if you can't instantiate a super
                // instance.
            }
        }
        else {
            safelyCheckSuperProperties(subjectCreator.plain(), subjectCreator.withAllFieldsShallowlyChanged());

            if (versionedEntity) {
                // If it's a versioned entity with an id field that indicates "newness" if it has its default value,
                // we should give the id field(s) an actual value, otherwise it will never be equal to its equal super.
                Predicate<Field> p =
                        f -> !config.annotationCache().hasFieldAnnotation(type, f.getName(), SupportedAnnotations.ID);
                safelyCheckSuperProperties(
                    subjectCreator.withAllMatchingFieldsDefaulted(p),
                    subjectCreator.withAllFieldsShallowlyChanged());
            }
            else {
                safelyCheckSuperProperties(
                    subjectCreator.withAllFieldsDefaulted(),
                    subjectCreator.withAllFieldsShallowlyChanged());
            }
        }
    }

    private void safelyCheckSuperProperties(T reference, T shallowScrambled) {
        if (strictnessSuppressed) {
            return;
        }

        Object equalSuper = getEqualSuper(reference);

        try {
            checkSuperProperties(reference, equalSuper, shallowScrambled);
        }
        catch (AbstractMethodError | NullPointerException ignored) {
            // In these cases, we'll assume all super properties hold.
            // The problems we test for, can never occur anyway if you can't instantiate a super
            // instance.
        }
    }

    private void checkSuperProperties(T reference, Object equalSuper, T shallow) {
        Formatter symmetryFormatter =
                Formatter.of("Symmetry:\n  %%\ndoes not equal superclass instance\n  %%", reference, equalSuper);
        assertTrue(symmetryFormatter, reference.equals(equalSuper) && equalSuper.equals(reference));

        Formatter transitivityFormatter =
                Formatter.of("""
                             Transitivity:
                               %%
                             and
                               %%
                             both equal superclass instance
                               %%
                             which implies they equal each other.""", reference, shallow, equalSuper);
        assertTrue(
            transitivityFormatter,
            reference.equals(shallow) || reference.equals(equalSuper) != equalSuper.equals(shallow));

        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        int equalSuperHashCode = cachedHashCodeInitializer.getInitializedHashCode(equalSuper);
        Formatter superclassFormatter = Formatter
                .of(
                    "Superclass: hashCode for\n  %% (%%)\nshould be equal to hashCode for superclass instance\n  %% (%%)",
                    reference,
                    referenceHashCode,
                    equalSuper,
                    equalSuperHashCode);
        assertTrue(superclassFormatter, referenceHashCode == equalSuperHashCode);
    }

    private void checkSubclass() {
        if (typeIsFinal || typeIsSealed || strictnessSuppressed) {
            return;
        }

        T reference = subjectCreator.plain();
        Class<? extends T> subclass = determineSubclass(reference, config.subclass(), config.subclassFactory());
        T equalSub = getEqualSub(reference, subclass, config.subclassFactory());

        if (config.usingGetClass()) {
            Formatter formatter =
                    Formatter.of("""
                                 Subclass: object is equal to an instance of a trivial subclass with equal fields:
                                   %%
                                 This should not happen when using getClass().""", equalSub);
            assertFalse(formatter, reference.equals(equalSub));
        }
        else {
            Formatter formatter =
                    Formatter.of("""
                                 Subclass: object is not equal to an instance of a trivial subclass with equal fields:
                                   %%
                                 Maybe you forgot to add usingGetClass(). Otherwise, consider\
                                  making the class final or use EqualsVerifier.simple().""", equalSub);
            assertTrue(formatter, reference.equals(equalSub));
        }
    }

    private void checkRedefinedSubclass() {
        if (typeIsFinal || typeIsSealed || !hasRedefinedSubclass) {
            return;
        }

        if (classProbe.isMethodFinal("equals", Object.class)) {
            fail(
                Formatter
                        .of(
                            "Subclass: %% has a final equals method.\nNo need to supply a redefined subclass.",
                            type.getSimpleName()));
        }

        T reference = subjectCreator.plain();
        T redefinedSub = getEqualSub(reference, config.redefinedSubclass(), config.redefinedSubclassFactory());
        checkMatchingValues(reference, redefinedSub, "subclass");
        assertFalse(
            Formatter.of("Subclass:\n  %%\nequals subclass instance\n  %%", reference, redefinedSub),
            reference.equals(redefinedSub));
    }

    private void checkFinalEqualsMethod() {
        boolean isEntity = config.annotationCache().hasClassAnnotation(type, SupportedAnnotations.ENTITY);
        if (strictnessSuppressed || isEntity || typeIsFinal || hasRedefinedSubclass) {
            return;
        }

        boolean equalsIsFinal = classProbe.isMethodFinal("equals", Object.class);
        boolean hashCodeIsFinal = classProbe.isMethodFinal("hashCode");

        if (config.usingGetClass()) {
            assertEquals(
                Formatter.of("Finality: equals and hashCode must both be final or both be non-final."),
                equalsIsFinal,
                hashCodeIsFinal);
        }
        else {
            Formatter equalsFormatter = Formatter.of("""
                                                     Subclass: equals is not final.
                                                     Make your class or your equals method final, or supply an\
                                                      instance of a redefined subclass using withRedefinedSubclass\
                                                      if equals cannot be final.""");
            assertTrue(equalsFormatter, equalsIsFinal);

            Formatter hashCodeFormatter = Formatter.of("""
                                                       Subclass: hashCode is not final.
                                                       Make your class or your hashCode method final, or supply an\
                                                        instance of a redefined subclass using withRedefinedSubclass\
                                                        if hashCode cannot be final.""");
            assertTrue(hashCodeFormatter, hashCodeIsFinal);
        }
    }

    private <A, B extends A> void checkMatchingValues(A superObject, B subObject, String which) {
        for (var p : FieldIterable.ofIgnoringStatic(superObject.getClass())) {
            var superValue = p.getValue(superObject);
            var subValue = p.getValue(subObject);
            assertTrue(
                Formatter.of("""
                             Provided factory is incorrect; redefined %% doesn't match:
                               %%
                             and
                               %%
                             don't have matching values for field %%.""", which, superObject, subObject, p.getName()),
                Objects.equals(superValue, subValue));
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends T> determineSubclass(
            T reference,
            Class<? extends T> subclass,
            InstanceFactory<? extends T> subclassFactory) {
        if (subclass != null) {
            return subclass;
        }
        if (subclassFactory != null) {
            return null;
        }

        // Don't use type directly, as reference may already be a subclass if type was abstract
        Class<T> realClass = (Class<T>) reference.getClass();
        return SubtypeManager.giveDynamicSubclass(realClass);
    }

    private T getEqualSub(T reference, Class<? extends T> subclass, InstanceFactory<? extends T> subclassFactory) {
        if (subclass != null) {
            return subjectCreator.copyIntoSubclass(reference, subclass, null);
        }
        if (subclassFactory != null) {
            var result = subjectCreator.copyIntoSubclass(reference, null, subclassFactory);
            assertFalse(
                Formatter
                        .of(
                            "Given subclassFactory constructs a %%, but must construct a subclass of %%.",
                            type.getSimpleName(),
                            type.getSimpleName()),
                type.equals(result.getClass()));
            return result;
        }
        throw new EqualsVerifierInternalBugException(
                "Neither a subclass nor a subclassFactory was given. This should not happen!");
    }

    private Object getEqualSuper(T reference) {
        var result = subjectCreator.copyIntoSuperclass(reference, config.redefinedSuperclassFactory());
        assertTrue(
            Formatter
                    .of(
                        "Given superclassFactory constructs a %%, but must construct the direct superclass of %%.",
                        result.getClass().getSimpleName(),
                        type.getSimpleName()),
            result.getClass().equals(type.getSuperclass())
                    || (type.getSuperclass().isAssignableFrom(result.getClass())
                            && result.getClass().getSimpleName().contains("$$DynamicSubclass$")));
        return result;
    }
}
