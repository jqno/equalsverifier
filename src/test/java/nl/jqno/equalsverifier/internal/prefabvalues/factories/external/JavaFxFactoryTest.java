package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JavaFxFactoryTest {
    @Test
    public void maintainCoverageOnJdksThatDontHaveJavafx() {
        assertNotNull(JavaFxFactory.getFactoryCache());
    }
}
