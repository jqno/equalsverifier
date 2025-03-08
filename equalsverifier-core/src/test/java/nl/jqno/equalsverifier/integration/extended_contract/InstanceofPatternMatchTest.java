package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class InstanceofPatternMatchTest {

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
            return this == obj || (obj instanceof Point p && p.x == x && p.y == y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
