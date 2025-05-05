package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

class FactoriesTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Factories.class);
    }
}
