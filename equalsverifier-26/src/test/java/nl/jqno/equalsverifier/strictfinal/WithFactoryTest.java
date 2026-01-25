package nl.jqno.equalsverifier.strictfinal;

import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class WithFactoryTest {
    @Test
    void simpleClass() {
        EqualsVerifier
                .forClass(Uninstantiable.class)
                .withFactory(
                    values -> new Uninstantiable(values.getString("s") == null ? null : List.of(values.getString("s")),
                            values.getString("t")))
                .verify();
    }
}
