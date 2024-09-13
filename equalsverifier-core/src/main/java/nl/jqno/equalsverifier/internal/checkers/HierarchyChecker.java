package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.instantiation.ClassProbe;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.*;

public class HierarchyChecker<T> implements Checker {

    private final Configuration<T> config;
    private final Class<T> type;
    private final SubjectCreator<T> subjectCreator;
    private final ClassProbe<T> classProbe;
    private final Class<? extends T> redefinedSubclass;
    private final boolean strictnessSuppressed;
    private final boolean hasRedefinedSubclass;
    private final boolean typeIsFinal;
    private final boolean typeIsSealed;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public HierarchyChecker(Context<T> context) {
        this.config = context.getConfiguration();

        this.strictnessSuppressed =
            config.getWarningsToSuppress().contains(Warning.STRICT_INHERITANCE);
        this.hasRedefinedSubclass = config.getRedefinedSubclass() != null;
        if (strictnessSuppressed && hasRedefinedSubclass) {
            fail(
                Formatter.of(
                    "withRedefinedSubclass and weakInheritanceCheck are mutually exclusive."
                )
            );
        }

        this.type = context.getType();
        this.subjectCreator = context.getSubjectCreator();
        this.classProbe = context.getClassProbe();
        this.redefinedSubclass = config.getRedefinedSubclass();
        this.typeIsFinal = Modifier.isFinal(type.getModifiers());
        this.typeIsSealed = classProbe.isSealed();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
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

        if (config.hasRedefinedSuperclass() || config.isUsingGetClass()) {
            T reference = subjectCreator.plain();
            Object equalSuper = getEqualSuper(reference);

            Formatter formatter = Formatter.of(
                "Redefined superclass:\n" +
                "  %%\nshould not equal superclass instance\n  %%\nbut it does.",
                reference,
                equalSuper
            );
            try {
                assertFalse(
                    formatter,
                    reference.equals(equalSuper) || equalSuper.equals(reference)
                );
            } catch (AbstractMethodError ignored) {
                // In this case, we'll assume all super properties hold.
                // The problems we test for, can never occur anyway if you can't instantiate a super
                // instance.
            }
        } else {
            safelyCheckSuperProperties(
                subjectCreator.plain(),
                subjectCreator.withAllFieldsShallowlyChanged()
            );
            safelyCheckSuperProperties(
                subjectCreator.withAllFieldsDefaulted(),
                subjectCreator.withAllFieldsShallowlyChanged()
            );
        }
    }

    @SuppressFBWarnings(
        value = "DCN_NULLPOINTER_EXCEPTION",
        justification = "The equals method in a superclasses can throw an NPE, but it's a specific non-goal to do something with that here."
    )
    private void safelyCheckSuperProperties(T reference, T shallowScrambled) {
        if (strictnessSuppressed) {
            return;
        }

        Object equalSuper = getEqualSuper(reference);

        try {
            checkSuperProperties(reference, equalSuper, shallowScrambled);
        } catch (AbstractMethodError | NullPointerException ignored) {
            // In these cases, we'll assume all super properties hold.
            // The problems we test for, can never occur anyway if you can't instantiate a super
            // instance.
        }
    }

    private void checkSuperProperties(T reference, Object equalSuper, T shallow) {
        Formatter symmetryFormatter = Formatter.of(
            "Symmetry:\n  %%\ndoes not equal superclass instance\n  %%",
            reference,
            equalSuper
        );
        assertTrue(symmetryFormatter, reference.equals(equalSuper) && equalSuper.equals(reference));

        Formatter transitivityFormatter = Formatter.of(
            "Transitivity:\n" +
            "  %%\nand\n  %%\nboth equal superclass instance\n  %%\nwhich implies they equal each other.",
            reference,
            shallow,
            equalSuper
        );
        assertTrue(
            transitivityFormatter,
            reference.equals(shallow) || reference.equals(equalSuper) != equalSuper.equals(shallow)
        );

        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        int equalSuperHashCode = cachedHashCodeInitializer.getInitializedHashCode(equalSuper);
        Formatter superclassFormatter = Formatter.of(
            "Superclass: hashCode for\n  %% (%%)\nshould be equal to hashCode for superclass instance\n  %% (%%)",
            reference,
            referenceHashCode,
            equalSuper,
            equalSuperHashCode
        );
        assertTrue(superclassFormatter, referenceHashCode == equalSuperHashCode);
    }

    private Object getEqualSuper(T reference) {
        return subjectCreator.copyIntoSuperclass(reference);
    }

    private void checkSubclass() {
        if (typeIsFinal || typeIsSealed || strictnessSuppressed) {
            return;
        }

        T reference = subjectCreator.plain();

        @SuppressWarnings("unchecked")
        Class<T> anonymousSubclass = (Class<T>) Instantiator.giveDynamicSubclass(
            reference.getClass() // don't use type directly, as reference may already be a subclass if type was abstract
        );
        T equalSub = subjectCreator.copyIntoSubclass(reference, anonymousSubclass);

        if (config.isUsingGetClass()) {
            Formatter formatter = Formatter.of(
                "Subclass: object is equal to an instance of a trivial subclass with" +
                " equal fields:\n  %%\nThis should not happen when using getClass().",
                reference
            );
            assertFalse(formatter, reference.equals(equalSub));
        } else {
            Formatter formatter = Formatter.of(
                "Subclass: object is not equal to an instance of a trivial subclass" +
                " with equal fields:\n  %%\n" +
                "Maybe you forgot to add usingGetClass(). Otherwise, consider" +
                " making the class final or use EqualsVerifier.simple().",
                reference
            );
            assertTrue(formatter, reference.equals(equalSub));
        }
    }

    private void checkRedefinedSubclass() {
        if (typeIsFinal || typeIsSealed || !hasRedefinedSubclass) {
            return;
        }

        if (methodIsFinal("equals", Object.class)) {
            fail(
                Formatter.of(
                    "Subclass: %% has a final equals method.\nNo need to supply a redefined subclass.",
                    type.getSimpleName()
                )
            );
        }

        T reference = subjectCreator.plain();
        T redefinedSub = subjectCreator.copyIntoSubclass(reference, redefinedSubclass);
        assertFalse(
            Formatter.of(
                "Subclass:\n  %%\nequals subclass instance\n  %%",
                reference,
                redefinedSub
            ),
            reference.equals(redefinedSub)
        );
    }

    private void checkFinalEqualsMethod() {
        boolean isEntity = config
            .getAnnotationCache()
            .hasClassAnnotation(type, SupportedAnnotations.ENTITY);
        if (strictnessSuppressed || isEntity || typeIsFinal || hasRedefinedSubclass) {
            return;
        }

        boolean equalsIsFinal = methodIsFinal("equals", Object.class);
        boolean hashCodeIsFinal = methodIsFinal("hashCode");

        if (config.isUsingGetClass()) {
            assertEquals(
                Formatter.of(
                    "Finality: equals and hashCode must both be final or both be non-final."
                ),
                equalsIsFinal,
                hashCodeIsFinal
            );
        } else {
            Formatter equalsFormatter = Formatter.of(
                "Subclass: equals is not final.\n" +
                "Make your class or your equals method final, or supply an" +
                " instance of a redefined subclass using withRedefinedSubclass" +
                " if equals cannot be final."
            );
            assertTrue(equalsFormatter, equalsIsFinal);

            Formatter hashCodeFormatter = Formatter.of(
                "Subclass: hashCode is not final.\n" +
                "Make your class or your hashCode method final, or supply an" +
                " instance of a redefined subclass using withRedefinedSubclass" +
                " if hashCode cannot be final."
            );
            assertTrue(hashCodeFormatter, hashCodeIsFinal);
        }
    }

    private boolean methodIsFinal(String methodName, Class<?>... parameterTypes) {
        return rethrow(() -> {
            Method method = type.getMethod(methodName, parameterTypes);
            return Modifier.isFinal(method.getModifiers());
        });
    }
}
