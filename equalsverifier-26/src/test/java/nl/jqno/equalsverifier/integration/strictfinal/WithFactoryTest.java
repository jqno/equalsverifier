package nl.jqno.equalsverifier.integration.strictfinal;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class WithFactoryTest {
    @Test
    void succeed_whenClassIsFinal() {
        EqualsVerifier
                .forClass(FinalNonConstructable.class)
                .withFactory(v -> new FinalNonConstructable("" + v.getInt("i")))
                .verify();
    }

    @Test
    void succeed_whenClassCanBeSubclassed_givenConstructableSubclass() {
        EqualsVerifier
                .forClass(NonConstructableParent.class)
                .withFactory(
                    v -> new NonConstructableParent("" + v.getInt("i")),
                    ConstructableSubForNonConstructableParent.class)
                .verify();
    }

    @Test
    void succeed_whenClassCanBeSubclassed_givenNonConstructableSubclassAndExtraFactory() {
        EqualsVerifier
                .forClass(NonConstructableParent.class)
                .withFactory(
                    v -> new NonConstructableParent("" + v.getInt("i")),
                    v -> new NonConstructableSubForNonConstructableParent("" + v.getInt("i")))
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

    static class NonConstructableParent {
        private final int i;

        public NonConstructableParent(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof NonConstructableParent other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }

    static final class ConstructableSubForNonConstructableParent extends NonConstructableParent {
        public ConstructableSubForNonConstructableParent(int i) {
            super("" + i);
        }
    }

    static final class NonConstructableSubForNonConstructableParent extends NonConstructableParent {
        public NonConstructableSubForNonConstructableParent(String i) {
            super(i);
        }
    }
}
