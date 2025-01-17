package it;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class EverythingWorksInTheModularWorldTest {

    @Test
    void classCanBeVerified() {
        EqualsVerifier.forClass(ClassPoint.class).verify();
    }

    final class ClassPoint {
        private final int x;
        private final int y;

        private ClassPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ClassPoint other && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
