package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class SealedTypesRecursionTest {

    @Test
    public void testEV() {
        EqualsVerifier
            .forClass(A.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    class A {

        public I sealedClassField;

        @Override
        public int hashCode() {
            return Objects.hash(sealedClassField);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof A)) {
                return false;
            }
            A other = (A) obj;
            return Objects.equals(sealedClassField, other.sealedClassField);
        }
    }

    public sealed interface I permits E {}

    public final class E implements I {

        public A referenceToA;

        @Override
        public int hashCode() {
            return Objects.hash(referenceToA);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof E)) {
                return false;
            }
            E other = (E) obj;
            return Objects.equals(referenceToA, other.referenceToA);
        }
    }
}
