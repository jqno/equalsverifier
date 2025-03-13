package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

class ListBuildersTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(ListBuilders.class);
    }
}
