package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.ModeInstance;

/**
 * Provides a number of modes that influence how {@code EqualsVerifier} operates.
 *
 * @since 4.0
 */
public sealed interface Mode permits ModeInstance {

    /**
     * Signals that EqualsVerifier should not use Mockito, even if it's available on the classpath or modulepath.
     *
     * @return The skipMockito mode.
     *
     * @since 4.0
     */
    public static Mode skipMockito() {
        return ModeInstance.SKIP_MOCKITO;
    }

    /**
     * Forces that reflection on final fields is impossible. This is useful when preparing to migrate to a JDK where JEP
     * 500 ("final means final") is active.
     *
     * @return The finalMeansFinal mode.
     *
     * @since 4.4
     */
    public static Mode finalMeansFinal() {
        return ModeInstance.FINAL_MEANS_FINAL;
    }
}
