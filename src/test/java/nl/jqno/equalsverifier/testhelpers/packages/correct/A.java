package nl.jqno.equalsverifier.testhelpers.packages.correct;

public final class A {

    private final int x;
    private final int y;

    public A(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof A)) {
            return false;
        }
        A p = (A) obj;
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
