package nl.jqno.equalsverifier;

import java.util.function.Predicate;

import nl.jqno.equalsverifier.internal.reflection.PackageScanOptions;

/**
 * Provides a number of options that can be set in {@link EqualsVerifier#forPackage(String, ScanOption...)}. These
 * options affect the way in which EqualsVerifier scans the given package.
 *
 * @since 3.19
 */
public sealed interface ScanOption permits PackageScanOptions.O, PackageScanOptions.MustExtend,
        PackageScanOptions.ExceptClasses, PackageScanOptions.ExclusionPredicate {

    /**
     * Signals that not just the given package should be scanned, but also all of its sub-packages.
     *
     * @return The 'recursive' flag.
     *
     * @since 3.19
     */
    public static ScanOption recursive() {
        return PackageScanOptions.O.RECURSIVE;
    }

    /**
     * Signals that packages from external jars, which can't be scanned, will be ignored rather than throw an exception.
     *
     * @return The 'ignore external jars' flag.
     *
     * @since 3.19
     */
    public static ScanOption ignoreExternalJars() {
        return PackageScanOptions.O.IGNORE_EXTERNAL_JARS;
    }

    /**
     * Finds only classes that extend or implement the given type.
     *
     * @param type The type that all classes must extend or implement.
     * @return The 'mustExtend' flag with the associated type.
     *
     * @since 3.19
     */
    public static ScanOption mustExtend(Class<?> type) {
        return new PackageScanOptions.MustExtend(type);
    }

    /**
     * Removes the given type or types from the list of types to verify.
     *
     * @param type A type to remove from the list of types to verify.
     * @param more More types to remove from the list of types to verify.
     * @return The 'except' flag with the associated types.
     *
     * @since 3.19
     */
    public static ScanOption except(Class<?> type, Class<?>... more) {
        return new PackageScanOptions.ExceptClasses(type, more);
    }

    /**
     * Removes all types matching the given Predicate.
     *
     * @param exclusionPredicate A Predicate matching classes to remove from the list of types to verify.
     * @return The 'except' flag with the associated Predicate.
     *
     * @since 3.19
     */
    public static ScanOption except(Predicate<Class<?>> exclusionPredicate) {
        return new PackageScanOptions.ExclusionPredicate(exclusionPredicate);
    }
}
