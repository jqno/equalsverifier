package nl.jqno.equalsverifier.strictfinal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class NonfinalReflectionTest {

    @Test
    void succeeds_whenSutHasStaticNonfinalField() {
        assertThat(StaticNonfinalFieldContainer.staticI).isEqualTo(10);
        EqualsVerifier.forClass(StaticNonfinalFieldContainer.class).verify();
        assertThat(StaticNonfinalFieldContainer.staticI).isEqualTo(10);
    }

    @Test
    void succeedsWithoutFactory_whenSutIsJpaEntity() {
        EqualsVerifier.forClass(JpaEntity.class).verify();
    }

    @Test
    void succeedsWithoutFactory_whenSutIsJakartaEntity() {
        EqualsVerifier.forClass(JakartaEntity.class).verify();
    }

    static final class StaticNonfinalFieldContainer {
        private final int i;
        private static int staticI = 10;

        public StaticNonfinalFieldContainer(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StaticNonfinalFieldContainer other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    @nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Entity
    static class JpaEntity {
        @nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Id
        private int id;
        private String s;

        public JpaEntity() {}

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof JpaEntity other && Objects.equals(s, other.s);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(s);
        }
    }

    @jakarta.persistence.Entity
    static class JakartaEntity {
        @jakarta.persistence.Id
        private int id;
        private String s;

        public JakartaEntity() {}

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof JakartaEntity other && Objects.equals(s, other.s);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(s);
        }
    }
}
