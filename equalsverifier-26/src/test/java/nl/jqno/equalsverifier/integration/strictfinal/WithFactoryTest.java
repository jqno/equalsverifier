package nl.jqno.equalsverifier.integration.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class WithFactoryTest {
    @Test
    void fail_whenClassIsFinal() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(FinalNonConstructable.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate FinalNonConstructable.")
                .hasMessageContaining("Use #withFactory()");
    }

    @Test
    void succeed_whenClassIsFinal_givenFactory() {
        EqualsVerifier
                .forClass(FinalNonConstructable.class)
                .withFactory(v -> new FinalNonConstructable("" + v.getInt("i")))
                .verify();
    }

    @Test
    void fail_whenClassIsFinal_givenFactoryIsIncorrect() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(FinalNonConstructable.class)
                    .withFactory(v -> new FinalNonConstructable("42"))
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Significant fields");
    }

    @Test
    void fail_whenClassCanBeSubclassed() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(NonConstructableParent.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate NonConstructableParent.")
                .hasMessageContaining("Use #withFactory()");
    }

    @Test
    void fail_whenClassCanBeSubclassed_givenParentFactoryButNotSubclassFactory() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(NonConstructableParent.class)
                    .withFactory(v -> new NonConstructableParent("" + v.getInt("i")))
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate a subclass of NonConstructableParent (attempted")
                .hasMessageContaining("Use an overload of #withFactory() to specify a subclass.");
    }

    @Test
    void fail_whenClassCanBeSubclassed_givenParentAndSubclassFactoryAreTheSame() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(NonConstructableParent.class)
                    .withFactory(
                        v -> new NonConstructableParent("" + v.getInt("i")),
                        v -> new NonConstructableParent("" + v.getInt("i")))
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(
                    "Given subclassFactory constructs a NonConstructableParent, but must construct a subclass of NonConstructableParent.");
    }

    @Test
    void succeed_whenClassCanBeSubclassed_givenParentAndSubclassFactory() {
        EqualsVerifier
                .forClass(NonConstructableParent.class)
                .withFactory(
                    v -> new NonConstructableParent("" + v.getInt("i")),
                    v -> new NonConstructableSubForNonConstructableParent("" + v.getInt("i")))
                .verify();
    }

    @Test
    void succeed_whenClassCanBeSubclassed_givenParentFactoryAndSuppressedWarning() {
        EqualsVerifier
                .forClass(NonConstructableParent.class)
                .withFactory(v -> new NonConstructableParent("" + v.getInt("i")))
                .suppress(Warning.STRICT_INHERITANCE)
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
