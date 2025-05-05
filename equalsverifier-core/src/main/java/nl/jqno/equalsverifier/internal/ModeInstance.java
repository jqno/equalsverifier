package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.Mode;

public final class ModeInstance implements Mode {

    private ModeInstance() {}

    public static final Mode SKIP_MOCKITO = new ModeInstance();
}
