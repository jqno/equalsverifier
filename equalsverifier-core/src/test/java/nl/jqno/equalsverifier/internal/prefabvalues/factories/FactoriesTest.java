package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

public class FactoriesTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Factories.class);
    }
}
