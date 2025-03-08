package nl.jqno.equalsverifier.testhelpers.packages.twoincorrect;

public final class IncorrectN {

    private final int x;
    private final int y;

    public IncorrectN(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IncorrectN)) {
            return false;
        }
        IncorrectN p = (IncorrectN) obj;
        return p.x == x && p.y != y;
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
