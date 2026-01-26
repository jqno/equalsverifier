package nl.jqno.equalsverifier.strictfinal;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class NonfinalReflectionTest {
    @Test
    void succeedsWithoutFactory_whenSutIsJpaEntity() {
        EqualsVerifier.forClass(JpaEntity.class).verify();
    }

    @Test
    void succeedsWithoutFactory_whenSutIsJakartaEntity() {
        EqualsVerifier.forClass(JakartaEntity.class).verify();
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
