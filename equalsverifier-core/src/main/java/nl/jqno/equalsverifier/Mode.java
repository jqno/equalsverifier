package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.ModeInstance;

/**
 * Provides a number of modes that influence how {@code EqualsVerifier} operates.
 */
public sealed interface Mode permits ModeInstance {

    /**
     * Signals that EqualsVerifier should not use Mockito, even if it's available on the classpath or modulepath.
     *
     * @return The skipMockito mode.
     */
    public static Mode skipMockito() {
        return ModeInstance.SKIP_MOCKITO;
    }
}
