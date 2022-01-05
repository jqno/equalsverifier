package nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage;

public class IncorrectO {

    private final int x;
    private final int y;

    public IncorrectO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IncorrectO)) {
            return false;
        }
        IncorrectO p = (IncorrectO) obj;
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
