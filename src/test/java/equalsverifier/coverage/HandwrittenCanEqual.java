package equalsverifier.coverage;

import equalsverifier.testhelpers.types.Color;

public class HandwrittenCanEqual {
    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof Point;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) {
                return false;
            }
            Point other = (Point)obj;
            return other.canEqual(this) && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }
    }

    public static class ColorPoint extends Point {
        private final Color color;

        public ColorPoint(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof ColorPoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ColorPoint)) {
                return false;
            }
            ColorPoint other = (ColorPoint)obj;
            return other.canEqual(this) && super.equals(other) && (color == null ? other.color == null : color.equals(other.color));
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + super.hashCode();
            result = 31 * result + (color == null ? 0 : color.hashCode());
            return result;
        }
    }

    public static final class EndPoint extends ColorPoint {
        public EndPoint(int x, int y, Color color) {
            super(x, y, color);
        }

        @Override
        public boolean canEqual(Object obj) {
            return false;
        }
    }
}
