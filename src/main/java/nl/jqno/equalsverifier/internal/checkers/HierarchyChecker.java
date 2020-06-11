package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class HierarchyChecker<T> implements Checker {
    private final Configuration<T> config;
    private final Class<T> type;
    private final TypeTag typeTag;
    private final ClassAccessor<T> classAccessor;
    private final Class<? extends T> redefinedSubclass;
    private final boolean typeIsFinal;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public HierarchyChecker(Configuration<T> config) {
        this.config = config;

        if (config.getWarningsToSuppress().contains(Warning.STRICT_INHERITANCE)
                && config.getRedefinedSubclass() != null) {
            fail(
                    Formatter.of(
                            "withRedefinedSubclass and weakInheritanceCheck are mutually exclusive."));
        }

        this.type = config.getType();
        this.typeTag = config.getTypeTag();
        this.classAccessor = config.getClassAccessor();
        this.redefinedSubclass = config.getRedefinedSubclass();
        this.typeIsFinal = Modifier.isFinal(type.getModifiers());
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
        ClassAccessor<? super T> superAccessor = classAccessor.getSuperAccessor();
        if (superAccessor.isEqualsInheritedFromObject()) {
            return;
        }

        if (config.hasRedefinedSuperclass() || config.isUsingGetClass()) {
            T reference = classAccessor.getRedObject(typeTag);
            Object equalSuper = getEqualSuper(reference);

            Formatter formatter =
                    Formatter.of(
                            "Redefined superclass:\n  %%\nshould not equal superclass instance\n  %%\nbut it does.",
                            reference, equalSuper);
            try {
                assertFalse(
                        formatter, reference.equals(equalSuper) || equalSuper.equals(reference));
            } catch (AbstractMethodError ignored) {
                // In this case, we'll assume all super properties hold.
                // The problems we test for, can never occur anyway if you can't instantiate a super
                // instance.
            }
        } else {
            safelyCheckSuperProperties(classAccessor.getRedAccessor(typeTag));
            safelyCheckSuperProperties(
                    classAccessor.getDefaultValuesAccessor(
                            typeTag, config.getNonnullFields(), config.getAnnotationCache()));
        }
    }

    private void safelyCheckSuperProperties(ObjectAccessor<T> referenceAccessor) {
        T reference = referenceAccessor.get();
        Object equalSuper = getEqualSuper(reference);

        T shallowCopy = referenceAccessor.copy();
        ObjectAccessor.of(shallowCopy).shallowScramble(config.getPrefabValues(), typeTag);

        try {
            checkSuperProperties(reference, equalSuper, shallowCopy);
        } catch (AbstractMethodError | NullPointerException ignored) {
            // In these cases, we'll assume all super properties hold.
            // The problems we test for, can never occur anyway if you can't instantiate a super
            // instance.
        }
    }

    private void checkSuperProperties(T reference, Object equalSuper, T shallow) {
        Formatter symmetryFormatter =
                Formatter.of(
                        "Symmetry:\n  %%\ndoes not equal superclass instance\n  %%",
                        reference, equalSuper);
        assertTrue(symmetryFormatter, reference.equals(equalSuper) && equalSuper.equals(reference));

        Formatter transitivityFormatter =
                Formatter.of(
                        "Transitivity:\n  %%\nand\n  %%\nboth equal superclass instance\n  %%\nwhich implies they equal each other.",
                        reference, shallow, equalSuper);
        assertTrue(
                transitivityFormatter,
                reference.equals(shallow)
                        || reference.equals(equalSuper) != equalSuper.equals(shallow));

        int referenceHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
        int equalSuperHashCode = cachedHashCodeInitializer.getInitializedHashCode(equalSuper);
        Formatter superclassFormatter =
                Formatter.of(
                        "Superclass: hashCode for\n  %% (%%)\nshould be equal to hashCode for superclass instance\n  %% (%%)",
                        reference, referenceHashCode, equalSuper, equalSuperHashCode);
        assertTrue(superclassFormatter, referenceHashCode == equalSuperHashCode);
    }

    private Object getEqualSuper(T reference) {
        return ObjectAccessor.of(reference, type.getSuperclass()).copy();
    }

    private void checkSubclass() {
        if (typeIsFinal || config.getWarningsToSuppress().contains(Warning.STRICT_INHERITANCE)) {
            return;
        }

        ObjectAccessor<T> referenceAccessor = classAccessor.getRedAccessor(typeTag);
        T reference = referenceAccessor.get();
        T equalSub = referenceAccessor.copyIntoAnonymousSubclass();

        if (config.isUsingGetClass()) {
            Formatter formatter =
                    Formatter.of(
                            "Subclass: object is equal to an instance of a trivial subclass with equal fields:"
                                    + "\n  %%\nThis should not happen when using getClass().",
                            reference);
            assertFalse(formatter, reference.equals(equalSub));
        } else {
            Formatter formatter =
                    Formatter.of(
                            "Subclass: object is not equal to an instance of a trivial subclass with equal fields:\n  %%\n"
                                    + "Maybe you forgot to add usingGetClass(). Otherwise, consider making the class final.",
                            reference);
            assertTrue(formatter, reference.equals(equalSub));
        }
    }

    private void checkRedefinedSubclass() {
        if (typeIsFinal || redefinedSubclass == null) {
            return;
        }

        if (methodIsFinal("equals", Object.class)) {
            fail(
                    Formatter.of(
                            "Subclass: %% has a final equals method.\nNo need to supply a redefined subclass.",
                            type.getSimpleName()));
        }

        ObjectAccessor<T> referenceAccessor = classAccessor.getRedAccessor(typeTag);
        T reference = referenceAccessor.get();
        T redefinedSub = referenceAccessor.copyIntoSubclass(redefinedSubclass);
        assertFalse(
                Formatter.of(
                        "Subclass:\n  %%\nequals subclass instance\n  %%", reference, redefinedSub),
                reference.equals(redefinedSub));
    }

    private void checkFinalEqualsMethod() {
        boolean ignore =
                config.getWarningsToSuppress().contains(Warning.STRICT_INHERITANCE)
                        || config.getAnnotationCache()
                                .hasClassAnnotation(type, SupportedAnnotations.ENTITY)
                        || typeIsFinal
                        || redefinedSubclass != null;
        if (ignore) {
            return;
        }

        boolean equalsIsFinal = methodIsFinal("equals", Object.class);
        boolean hashCodeIsFinal = methodIsFinal("hashCode");

        if (config.isUsingGetClass()) {
            assertEquals(
                    Formatter.of(
                            "Finality: equals and hashCode must both be final or both be non-final."),
                    equalsIsFinal,
                    hashCodeIsFinal);
        } else {
            Formatter equalsFormatter =
                    Formatter.of(
                            "Subclass: equals is not final."
                                    + "\nMake your class or your equals method final,"
                                    + " or supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.");
            assertTrue(equalsFormatter, equalsIsFinal);

            Formatter hashCodeFormatter =
                    Formatter.of(
                            "Subclass: hashCode is not final."
                                    + "\nMake your class or your hashCode method final,"
                                    + " or supply an instance of a redefined subclass using withRedefinedSubclass if hashCode cannot be final.");
            assertTrue(hashCodeFormatter, hashCodeIsFinal);
        }
    }

    private boolean methodIsFinal(String methodName, Class<?>... parameterTypes) {
        try {
            Method method = type.getMethod(methodName, parameterTypes);
            return Modifier.isFinal(method.getModifiers());
        } catch (SecurityException | NoSuchMethodException e) {
            throw new ReflectionException(
                    "Should never occur: cannot find " + type.getName() + "." + methodName);
        }
    }
}
