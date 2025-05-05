package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Objects;

public final class PreconditionTypeHelper {
    private PreconditionTypeHelper() {}

    public static final class SinglePrecondition {

        private final FinalPoint point;

        public SinglePrecondition(FinalPoint point) {
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (point == null || point.getX() != 3) {
                throw new IllegalArgumentException("x coordinate must be 3! But was " + point);
            }
            if (!(obj instanceof SinglePrecondition)) {
                return false;
            }
            SinglePrecondition other = (SinglePrecondition) obj;
            return Objects.equals(point, other.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }

    public record SinglePreconditionRecord(int i) {
        public SinglePreconditionRecord {
            if (i < 100 || i > 200) {
                throw new IllegalArgumentException("i must be between 100 and 200! But was " + i);
            }
        }
    }

    public static final class DualPrecondition {

        private final int x;
        private final int y;

        public DualPrecondition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (x < 100 || x > 200) {
                throw new IllegalArgumentException("x must be between 100 and 200! But was " + x);
            }
            if (y < 500 || y > 600) {
                throw new IllegalArgumentException("y must be between 500 and 600! But was " + y);
            }
            if (!(obj instanceof DualPrecondition)) {
                return false;
            }
            DualPrecondition other = (DualPrecondition) obj;
            return Objects.equals(x, other.x) && Objects.equals(y, other.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public record DualPreconditionRecord(int x, int y) {
        public DualPreconditionRecord {
            if (x < 100 || x > 200) {
                throw new IllegalArgumentException("x must be between 100 and 200! But was " + x);
            }
            if (y < 500 || y > 600) {
                throw new IllegalArgumentException("y must be between 500 and 600! But was " + y);
            }
        }
    }

    public static final class StringPrecondition {

        private final String s;

        public StringPrecondition(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (s != null && !s.startsWith("precondition:")) {
                throw new IllegalArgumentException("bad precondition: " + s);
            }
            if (!(obj instanceof StringPrecondition)) {
                return false;
            }
            StringPrecondition other = (StringPrecondition) obj;
            return Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }
}
