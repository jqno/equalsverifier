package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class SequencedCollectionsClassesTest {

    @Test
    public void succeed_whenClassContainsSequencedCollections() {
        EqualsVerifier.forClass(SequencedCollectionContainer.class).verify();
    }

    static final class SequencedCollectionContainer {

        private final SequencedCollection<String> collection;
        private final SequencedSet<String> set;
        private final SequencedMap<String, String> map;

        public SequencedCollectionContainer(
                SequencedCollection<String> collection,
                SequencedSet<String> set,
                SequencedMap<String, String> map) {
            this.collection = collection;
            this.set = set;
            this.map = map;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj
                    || (obj instanceof SequencedCollectionContainer other
                            && Objects.equals(collection, other.collection)
                            && Objects.equals(set, other.set)
                            && Objects.equals(map, other.map));
        }

        @Override
        public int hashCode() {
            // Trigger behavior that requires prefab values by calling abstract methods
            if (collection != null) {
                collection.reversed();
            }
            if (set != null) {
                set.reversed();
            }
            if (map != null) {
                map.reversed();
            }
            return Objects.hash(collection, set, map);
        }

        protected void callIterator(Iterable<?>... collections) {
            for (Iterable<?> c : collections) {
                if (c != null) {
                    c.iterator();
                }
            }
        }
    }
}
