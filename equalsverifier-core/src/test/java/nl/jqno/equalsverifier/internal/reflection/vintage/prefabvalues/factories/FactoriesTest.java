package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories;
import org.junit.jupiter.api.Test;

public class FactoriesTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Factories.class);
    }
}
