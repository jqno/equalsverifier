package nl.jqno.equalsverifier.mockito;

import java.util.EnumMap;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class SpecialCasesTest {

    @Test
    void succeed_whenFieldIsEnumMap_givenMockitoIsPresent() {
        EqualsVerifier.forClass(XEnumMapContainer.class).verify();
    }

    enum X {
        A, B, C
    }

    static final class XEnumMapContainer {
        private final EnumMap<X, String> map;

        public XEnumMapContainer(EnumMap<X, String> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof XEnumMapContainer other && Objects.equals(map, other.map);
        }

        @Override
        public int hashCode() {
            return Objects.hash(map);
        }
    }
}
