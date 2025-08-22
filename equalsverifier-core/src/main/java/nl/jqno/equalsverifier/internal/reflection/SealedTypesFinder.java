package nl.jqno.equalsverifier.internal.reflection;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;

public final class SealedTypesFinder {

    private SealedTypesFinder() {}

    public static <T, U extends T> Optional<Class<U>> findInstantiableSubclass(ClassProbe<T> probe) {
        return findInstantiablePermittedClass(probe);
    }

    private static <T, U extends T> Optional<Class<U>> findInstantiablePermittedClass(ClassProbe<T> probe) {
        if (!probe.isAbstract() || !probe.isSealed()) {
            @SuppressWarnings("unchecked")
            var result = (Class<U>) probe.getType();
            return Optional.of(result);
        }
        var permittedSubclasses = probe.getType().getPermittedSubclasses();
        if (permittedSubclasses == null) {
            return Optional.empty();
        }
        for (Class<?> permitted : permittedSubclasses) {
            @SuppressWarnings("unchecked")
            Class<U> subType = (Class<U>) permitted;
            ClassProbe<U> subProbe = ClassProbe.of(subType);

            var c = findInstantiablePermittedClass(subProbe);
            if (c.isPresent()) {
                return c;
            }
        }
        throw new EqualsVerifierInternalBugException(
                "Could not find a non-sealed subtype for " + probe.getType().getCanonicalName());
    }
}
