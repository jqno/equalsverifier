package nl.jqno.equalsverifier.testhelpers.packages.somerecursive;

public final class Nonrecursive {

    private final int x;
    private final int y;

    public Nonrecursive(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Nonrecursive)) {
            return false;
        }
        Nonrecursive p = (Nonrecursive) obj;
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
