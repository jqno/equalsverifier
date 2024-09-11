package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.instantiation.ClassProbe;
import org.junit.jupiter.api.Test;

public class ClassAccessorSealedTest {

    @Test
    public void isNotSealed() {
        var probe = new ClassProbe<>(SealedChild.class);
        assertFalse(probe.isSealed());
    }

    @Test
    public void isSealed() {
        var probe = new ClassProbe<>(SealedParent.class);
        assertTrue(probe.isSealed());
    }

    public abstract static sealed class SealedParent {}

    public static non-sealed class SealedChild extends SealedParent {}
}
