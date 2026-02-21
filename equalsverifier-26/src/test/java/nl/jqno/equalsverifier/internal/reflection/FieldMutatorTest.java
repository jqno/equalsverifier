package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import org.junit.jupiter.api.Test;

class FieldMutatorTest {

    private FieldProbe p;
    private FieldMutator sut;
    private Container o = new Container();

    @Test
    void setPrimitive() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("i"));
        sut = new FieldMutator(p);

        assertThatThrownBy(() -> sut.setNewValue(o, 1337))
                .isInstanceOf(EqualsVerifierInternalBugException.class)
                .hasMessageContaining("This is a bug in EqualsVerifier.")
                .hasMessageContaining("Not allowed to reflectively set final field Container.i.");
    }

    @SuppressWarnings("unused")
    static class Container {

        private final int i;

        public Container() {
            this.i = 10;
        }
    }
}
