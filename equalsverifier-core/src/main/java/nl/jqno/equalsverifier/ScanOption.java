package nl.jqno.equalsverifier;

import java.util.function.Predicate;

/**
 * Contains a number of options that can be set in {@link EqualsVerifier#forPackage(String, ScanOption...)}. These
 * options affect the way in which EqualsVerifier scans the given package.
 */
public interface ScanOption {

    /**
     * Signals that not just the given package should be scanned, but also all of its sub-packages.
     *
     * @return The 'recursive' flag.
     */
    public static ScanOption recursive() {
        return ScanOptions.O.RECURSIVE;
    }

    /**
     * Signals that packages from external jars, which can't be scanned, will be ignored rather than throw an exception.
     *
     * @return The 'ignore external jars' flag.
     */
    public static ScanOption ignoreExternalJars() {
        return ScanOptions.O.IGNORE_EXTERNAL_JARS;
    }

    /**
     * Finds only classes that extend or implement the given type.
     *
     * @param type The type that all classes must extend or implement.
     * @return The 'mustExtend' flag with the associated type.
     */
    public static ScanOption mustExtend(Class<?> type) {
        return new ScanOptions.MustExtend(type);
    }

    /**
     * Removes the given type or types from the list of types to verify.
     *
     * @param type A type to remove from the list of types to verify.
     * @param more More types to remove from the list of types to verify.
     * @return The 'except' flag with the associated types.
     */
    public static ScanOption except(Class<?> type, Class<?>... more) {
        return new ScanOptions.ExceptClasses(type, more);
    }

    /**
     * Removes all types matching the given Predicate.
     *
     * @param exclusionPredicate A Predicate matching classes to remove from the list of types to verify.
     * @return The 'except' flag with the associated Predicate.
     */
    public static ScanOption except(Predicate<Class<?>> exclusionPredicate) {
        return new ScanOptions.ExclusionPredicate(exclusionPredicate);
    }
}
