package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.Util;
import org.junit.jupiter.api.Test;

public class InstanceofPatternMatchTest {

    @Test
    void succeed_whenEqualsUsesInstanceofPatternMatch() {
        EqualsVerifier.forClass(Point.class).verify();
    }

    final class Point {

        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof Point p && p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Util.defaultHashCode(this);
        }
    }
}
