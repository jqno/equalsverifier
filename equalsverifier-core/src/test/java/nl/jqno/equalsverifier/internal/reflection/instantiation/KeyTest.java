package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class KeyTest {

    @Test
    public void keyEqualsAndHashCode() {
        EqualsVerifier
            .forClass(Key.class)
            .withPrefabValues(TypeTag.class, new TypeTag(Integer.class), new TypeTag(String.class))
            .verify();
    }

    @Test
    public void testToString() {
        Key k = new Key(String.class, "label");
        String actual = k.toString();
        assertTrue(actual.contains("String"));
        assertTrue(actual.contains("label"));
    }
}
