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
}
