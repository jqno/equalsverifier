package nl.jqno.equalsverifier;

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
}
