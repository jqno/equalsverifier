package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

public class ValidationsTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Validations.class);
    }
}
