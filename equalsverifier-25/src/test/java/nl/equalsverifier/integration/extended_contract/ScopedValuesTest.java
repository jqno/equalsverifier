package nl.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ScopedValuesTest {
    @Test
    void succeed_whenClassContainsScopedValue() {
        EqualsVerifier.forClass(ScopedValueContainer.class).verify();
    }

    static final class ScopedValueContainer {
        private final ScopedValue<String> scopedValue;

        public ScopedValueContainer(ScopedValue<String> scopedValue) {
            this.scopedValue = scopedValue;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ScopedValueContainer other && Objects.equals(scopedValue, other.scopedValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scopedValue);
        }
    }
}
