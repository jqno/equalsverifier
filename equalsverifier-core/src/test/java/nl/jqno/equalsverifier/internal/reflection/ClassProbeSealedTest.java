package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClassProbeSealedTest {

    @Test
    void isNotSealed() {
        var probe = ClassProbe.of(SealedChild.class);
        assertThat(probe.isSealed()).isFalse();
    }

    @Test
    void isSealed() {
        var probe = ClassProbe.of(SealedParent.class);
        assertThat(probe.isSealed()).isTrue();
    }

    public abstract static sealed class SealedParent {}

    public static non-sealed class SealedChild extends SealedParent {}
}
