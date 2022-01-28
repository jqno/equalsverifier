package nl.jqno.equalsverifier.internal.util;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * A wrapper around Objenesis. Objenesis keeps caches of objects it has instantiated, so we want a
 * way to easily re-use the same instance of `Objenesis`. This class reflects the usage in
 * {@link org.objenesis.ObjenesisHelper}, but with the added benefit that now we can reset the
 * caches if needed (for instance if the test framework used does some "clever" tricks with
 * ClassLoaders) by re-initializing the Objenesis instance.
 *
 * Note: I realise that a wrapper around a static reference is not very architecturally sound;
 * however, doing it properly would require major re-writes. Maybe some other time.
 */
public final class ObjenesisWrapper {

    private static Objenesis objenesis = new ObjenesisStd();

    private ObjenesisWrapper() {}

    public static Objenesis getObjenesis() {
        return objenesis;
    }

    public static void reset() {
        objenesis = new ObjenesisStd();
    }
}
