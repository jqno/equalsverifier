package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ShadowingTest {
    @Test
    void succeed_whenSuclassShadowsNonstaticField() {
        EqualsVerifier.forClass(NonstaticSub.class).verify();
    }

    @Test
    void succeed_whenSuclassShadowsStaticField() {
        EqualsVerifier.forClass(StaticSub.class).verify();
    }

    static class NonstaticSuper {
        private final String a;

        NonstaticSuper(String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }
    }

    static final class NonstaticSub extends NonstaticSuper {
        private final int a;

        NonstaticSub(String s, int a) {
            super(s);
            this.a = a;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonstaticSub)) {
                return false;
            }
            NonstaticSub other = (NonstaticSub) obj;
            return a == other.a && Objects.equals(getA(), other.getA());
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, getA());
        }
    }

    static class StaticSuper {
        private final String a;

        StaticSuper(String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }
    }

    static final class StaticSub extends StaticSuper {
        // CHECKSTYLE OFF: ConstantName
        public static final int a = 2;

        StaticSub(String a) {
            super(a);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StaticSub)) {
                return false;
            }
            StaticSub other = (StaticSub) obj;
            return Objects.equals(getA(), other.getA());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getA());
        }
    }
}
