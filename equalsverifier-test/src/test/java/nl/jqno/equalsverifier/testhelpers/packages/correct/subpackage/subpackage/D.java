package nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage;

public final class D {

    private final int x;
    private final int y;

    public D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof D)) {
            return false;
        }
        D p = (D) obj;
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
