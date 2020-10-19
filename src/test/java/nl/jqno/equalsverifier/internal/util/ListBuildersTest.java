package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

import org.junit.Test;

public class ListBuildersTest {
    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(ListBuilders.class);
    }
}
