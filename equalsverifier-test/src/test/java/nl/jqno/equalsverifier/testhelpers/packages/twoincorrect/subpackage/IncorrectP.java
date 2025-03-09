package nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage;

public final class IncorrectP {

    private final int x;
    private final int y;

    public IncorrectP(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IncorrectP)) {
            return false;
        }
        IncorrectP p = (IncorrectP) obj;
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
