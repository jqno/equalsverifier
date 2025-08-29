package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class WithPrefabValueSupplierTest {
    @Test
    void recordWithMutableBackReference() {
        EqualsVerifier
                .forClass(WithBackReference.class)
                .withPrefabValueSupplier(
                    BackReferenceContainer.class,
                    () -> new BackReferenceContainer(1),
                    () -> new BackReferenceContainer(2))
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    public record WithBackReference(BackReferenceContainer state) {

        public WithBackReference {
            state.setBackReference(this);
        }
    }

    public static final class BackReferenceContainer {
        private final int i;
        private Object backReference;

        public BackReferenceContainer(int i) {
            this.i = i;
        }

        public void setBackReference(Object newBackReference) {
            if (this.backReference != null) {
                throw new IllegalStateException("It's already set!");
            }
            this.backReference = newBackReference;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BackReferenceContainer other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

}
