package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

class PrefabValuesApiTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(PrefabValuesApi.class);
    }
}
