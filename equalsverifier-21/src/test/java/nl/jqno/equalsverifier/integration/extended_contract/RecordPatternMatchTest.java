package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.Util;
import org.junit.jupiter.api.Test;

public class RecordPatternMatchTest {

    @Test
    void succeed_whenEqualsUsesInstanceofPatternMatch() {
        EqualsVerifier.forClass(Point.class).verify();
    }

    record Point(int x, int y) {
        @Override
        public boolean equals(Object obj) {
            return (this == obj || (obj instanceof Point(int otherX, int otherY) && x == otherX && y == otherY));
        }

        @Override
        public int hashCode() {
            return Util.defaultHashCode(this);
        }
    }
}
