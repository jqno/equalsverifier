package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Map;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class MapEntrySubclassTest {

    @Test
    void fails_whenMapEntryHashCodeContractIsNotHonored() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(HashCodeContractNotHonored.class).verify())
            .assertFailure()
            .assertMessageContains("Map.Entry: hashCode for", "should be", "but was");
    }

    @Test
    void succeeds_whenMapEntryHashCodeContractIsHonored() {
        EqualsVerifier.forClass(HashCodeContractHonored.class).verify();
    }

    static final class HashCodeContractNotHonored<K, V> implements Map.Entry<K, V> {

        private final K key;
        private final V value;

        HashCodeContractNotHonored(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(key, other.getKey()) && Objects.equals(value, other.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        // CHECKSTYLE OFF: HiddenField
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
        // CHECKSTYLE ON: HiddenField
    }

    static final class HashCodeContractHonored<K, V> implements Map.Entry<K, V> {

        private final K key;
        private final V value;

        HashCodeContractHonored(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return Objects.equals(key, other.getKey()) && Objects.equals(value, other.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        // CHECKSTYLE OFF: HiddenField
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
        // CHECKSTYLE ON: HiddenField
    }
}
