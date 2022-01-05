package nl.jqno.equalsverifier.testhelpers.types;

public final class FinalPoint {

    private final int x;
    private final int y;

    public FinalPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FinalPoint)) {
            return false;
        }
        FinalPoint p = (FinalPoint) obj;
        return p.x == x && p.y == y;
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
