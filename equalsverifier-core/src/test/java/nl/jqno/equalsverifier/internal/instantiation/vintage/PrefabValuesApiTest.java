package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

public class PrefabValuesApiTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(PrefabValuesApi.class);
    }
}
