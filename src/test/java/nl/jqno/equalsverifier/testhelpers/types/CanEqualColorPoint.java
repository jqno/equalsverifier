package nl.jqno.equalsverifier.testhelpers.types;

import java.util.Objects;

public final class CanEqualColorPoint extends CanEqualPoint {

    private final Color color;

    public CanEqualColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean canEqual(Object obj) {
        return obj instanceof CanEqualColorPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CanEqualColorPoint)) {
            return false;
        }
        CanEqualColorPoint p = (CanEqualColorPoint) obj;
        return p.canEqual(this) && super.equals(p) && color == p.color;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(color) + (31 * super.hashCode());
    }

    @Override
    public String toString() {
        return super.toString() + "," + color;
    }
}
