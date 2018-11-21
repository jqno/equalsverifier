package equalsverifier.testhelpers.types;

import equalsverifier.testhelpers.annotations.Immutable;

@Immutable
public class ImmutableCanEqualPoint {
    private int x;
    private int y;

    public ImmutableCanEqualPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean canEqual(Object obj) {
        return obj instanceof ImmutableCanEqualPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImmutableCanEqualPoint)) {
            return false;
        }
        ImmutableCanEqualPoint p = (ImmutableCanEqualPoint)obj;
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
