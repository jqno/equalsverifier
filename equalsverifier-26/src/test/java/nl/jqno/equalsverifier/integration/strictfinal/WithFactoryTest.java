package nl.jqno.equalsverifier.integration.strictfinal;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class WithFactoryTest {
    @Test
    void succeed_whenClassIsFinal() {
        EqualsVerifier
                .forClass(FinalNonConstructable.class)
                .withFactory(values -> new FinalNonConstructable("" + values.getInt("i")))
                .verify();
    }

    @Test
    @Disabled
    void succeed_whenClassCanBeSubclassed() {
        EqualsVerifier
                .forClass(SubclassableNonConstructable.class)
                .withFactory(values -> new SubclassableNonConstructable("" + values.getInt("i")))
                .verify();
    }

    static final class FinalNonConstructable {
        private final int i;

        public FinalNonConstructable(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FinalNonConstructable other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static class SubclassableNonConstructable {
        private final int i;

        public SubclassableNonConstructable(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof SubclassableNonConstructable other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }
}
