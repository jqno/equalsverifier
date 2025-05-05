package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.reflection.Util;

public final class ExternalLibs {
    private ExternalLibs() {}

    public static boolean isMockitoAvailable() {
        return Util.classForName("org.mockito.Mockito") != null;
    }
}
