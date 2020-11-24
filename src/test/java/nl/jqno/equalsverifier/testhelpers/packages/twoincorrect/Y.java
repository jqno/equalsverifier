package nl.jqno.equalsverifier.testhelpers.packages.twoincorrect;

public final class Y {

    private final int x;
    private final int y;

    public Y(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Y)) {
            return false;
        }
        Y p = (Y) obj;
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
