package nl.jqno.equalsverifier.internal.reflection;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public final class SealedTypesFinder {

    private SealedTypesFinder() {}

    @SuppressWarnings("unchecked")
    public static <T> Optional<Class<T>> findInstantiableSubclass(ClassProbe<T> probe) {
        return findInstantiablePermittedClasses(probe).findFirst().map(c -> (Class<T>) c);
    }

    public static <T> Stream<Class<? extends T>> findInstantiableSubclasses(ClassProbe<T> probe) {
        return findInstantiablePermittedClasses(probe);
    }

    private static <T> Stream<Class<? extends T>> findInstantiablePermittedClasses(ClassProbe<T> probe) {
        if (!probe.isAbstract() || !probe.isSealed()) {
            return Stream.of(probe.getType());
        }

        var permittedSubclasses = probe.getType().getPermittedSubclasses();
        if (permittedSubclasses == null) {
            return Stream.empty();
        }

        return Arrays.stream(permittedSubclasses).flatMap(permitted -> {
            @SuppressWarnings("unchecked")
            ClassProbe<T> subProbe = (ClassProbe<T>) ClassProbe.of(permitted);
            return findInstantiablePermittedClasses(subProbe);
        });
    }
}
