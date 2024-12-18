package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ClassProbeSealedTest {

    @Test
    public void isNotSealed() {
        var probe = ClassProbe.of(SealedChild.class);
        assertFalse(probe.isSealed());
    }

    @Test
    public void isSealed() {
        var probe = ClassProbe.of(SealedParent.class);
        assertTrue(probe.isSealed());
    }

    public abstract static sealed class SealedParent {}

    public static non-sealed class SealedChild extends SealedParent {}
}
