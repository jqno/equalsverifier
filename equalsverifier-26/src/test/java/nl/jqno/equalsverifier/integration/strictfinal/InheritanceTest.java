package nl.jqno.equalsverifier.integration.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class InheritanceTest {

    @Test
    void succeed_whenClassHasRedefinedSubclass_givenConstructorsMatchFields() {
        EqualsVerifier.forClass(ConstructableSuper.class).withRedefinedSubclass(ConstructableSub.class).verify();
    }

    @Test
    void succeed_whenClassHasRedefinedSubclass_givenSubclassRequiresFactory() {
        EqualsVerifier
                .forClass(ConstructableSuper.class)
                .withRedefinedSubclass(v -> new NonConstructableSub(v.getInt("i"), "" + v.getInt("j")))
                .verify();
    }

    @Test
    void succeed_whenClassHasRedefinedSuperclass_givenConstructorsMatchFields() {
        EqualsVerifier.forClass(ConstructableSub.class).withRedefinedSuperclass().verify();
    }

    @Test
    void succeed_whenClassHasNonConstructableRedefinedSubclass_givenClassRequiresFactory() {
        EqualsVerifier
                .forClass(NonConstructableSuper.class)
                .withFactory(
                    v -> new NonConstructableSuper("" + v.getInt("i")),
                    TrivialConstructableSubclassForNonConstructableSuper.class)
                .withRedefinedSubclass(
                    v -> new NonConstructableSubForNonConstructableSuper("" + v.getInt("i"), v.getInt("j")))
                .verify();
    }

    @Test
    void succeed_whenClassHasConstructableRedefinedSubclass_givenClassRequiresFactory() {
        EqualsVerifier
                .forClass(NonConstructableSuper.class)
                .withFactory(
                    v -> new NonConstructableSuper("" + v.getInt("i")),
                    TrivialConstructableSubclassForNonConstructableSuper.class)
                .withRedefinedSubclass(ConstructableSubForNonConstructableSuper.class)
                .verify();
    }

    @Test
    void succeed_whenClassHasRedefinedSuperclass_givenSuperclassRequiresFactory() {
        EqualsVerifier
                .forClass(ConstructableSubForNonConstructableSuper.class)
                .withRedefinedSuperclass(v -> new NonConstructableSuper("" + v.getInt("i")))
                .verify();
    }

    @Test
    void fail_whenClassHasRedefinedSuperclass_givenSuperclassFactoryConstructsObject() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(ConstructableSubForNonConstructableSuper.class)
                    .withRedefinedSuperclass(v -> new Object())
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Given superclassFactory constructs a Object")
                .hasMessageContaining(
                    "must construct the direct superclass of ConstructableSubForNonConstructableSuper");
    }

    @Test
    void fail_whenClassHasRedefinedSuperclass_givenSuperclassFactoryConstructsSomethingCompletelyDifferent() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(ConstructableSubForNonConstructableSuper.class)
                    .withRedefinedSuperclass(v -> new FinalPoint(1, 2))
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Given superclassFactory constructs a FinalPoint")
                .hasMessageContaining(
                    "must construct the direct superclass of ConstructableSubForNonConstructableSuper");
    }

    static class ConstructableSuper {
        private final int i;

        public ConstructableSuper(int i) {
            this.i = i;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof ConstructableSuper;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructableSuper other && other.canEqual(this) && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static final class ConstructableSub extends ConstructableSuper {
        private final int j;

        public ConstructableSub(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof ConstructableSub;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructableSub other && other.canEqual(this) && super.equals(other) && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.i, j);
        }
    }

    static final class NonConstructableSub extends ConstructableSuper {
        private final int j;

        public NonConstructableSub(int i, String j) {
            super(i);
            this.j = Integer.valueOf(j);
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof NonConstructableSub;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NonConstructableSub other
                    && other.canEqual(this)
                    && super.equals(other)
                    && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.i, j);
        }
    }

    static class NonConstructableSuper {
        private final int i;

        public NonConstructableSuper(String i) {
            this.i = Integer.valueOf(i);
        }

        public boolean canEqual(Object obj) {
            return obj instanceof NonConstructableSuper;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NonConstructableSuper other && other.canEqual(this) && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static class TrivialConstructableSubclassForNonConstructableSuper extends NonConstructableSuper {
        public TrivialConstructableSubclassForNonConstructableSuper(int i) {
            super("" + i);
        }
    }

    static final class NonConstructableSubForNonConstructableSuper extends NonConstructableSuper {
        private final int j;

        public NonConstructableSubForNonConstructableSuper(String i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof NonConstructableSubForNonConstructableSuper;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NonConstructableSubForNonConstructableSuper other
                    && other.canEqual(this)
                    && super.equals(other)
                    && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.i, j);
        }
    }

    static final class ConstructableSubForNonConstructableSuper extends NonConstructableSuper {
        private final int j;

        public ConstructableSubForNonConstructableSuper(int i, int j) {
            super("" + i);
            this.j = j;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof ConstructableSubForNonConstructableSuper;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructableSubForNonConstructableSuper other
                    && other.canEqual(this)
                    && super.equals(other)
                    && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.i, j);
        }
    }
}
