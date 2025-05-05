package nl.jqno.equalsverifier.internal;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

class PrefabValuesApiTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(PrefabValuesApi.class);
    }
}
