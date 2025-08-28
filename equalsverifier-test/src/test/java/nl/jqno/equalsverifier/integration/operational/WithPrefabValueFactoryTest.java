package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class WithPrefabValueFactoryTest {
    @Test
    void testEqualsAndHashCodeCountryRecord() {
        EqualsVerifier
                .forClass(WithBackReference.class)
                .withPrefabValues(BackReferenceContainer.class, new BackReferenceContainer(), new BackReferenceContainer())
                .verify();
    }

    public record WithBackReference(BackReferenceContainer state) {

        public WithBackReference {
            state.setBackReference(this);
        }
    }

    public static final class BackReferenceContainer {
        private Object backReference;

        public void setBackReference(Object newBackReference) {
            if (this.backReference != null) {
                throw new IllegalStateException("It's already set!");
            }
            this.backReference = newBackReference;
        }
    }

}
