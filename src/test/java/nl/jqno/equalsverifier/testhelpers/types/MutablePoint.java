package nl.jqno.equalsverifier.testhelpers.types;

public class MutablePoint {
    private int x;
    private int y;

    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutablePoint)) {
            return false;
        }
        MutablePoint other = (MutablePoint) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return x + (31 * y);
    }
}
