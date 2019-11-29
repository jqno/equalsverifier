package nl.jqno.equalsverifier.testhelpers.types;

public class CanEqualPoint {
    private final int x;
    private final int y;

    public CanEqualPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean canEqual(Object obj) {
        return obj instanceof CanEqualPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CanEqualPoint)) {
            return false;
        }
        CanEqualPoint p = (CanEqualPoint) obj;
        return p.canEqual(this) && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        return x + (31 * y);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + x + "," + y;
    }
}
