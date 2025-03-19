package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.ModeInstance;

public sealed interface Mode permits ModeInstance {

    public static Mode skipMockito() {
        return ModeInstance.SKIP_MOCKITO;
    }
}
