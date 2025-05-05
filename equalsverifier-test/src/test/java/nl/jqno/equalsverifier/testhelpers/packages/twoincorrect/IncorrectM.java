package nl.jqno.equalsverifier.testhelpers.packages.twoincorrect;

public class IncorrectM {

    private final int x;
    private final int y;

    public IncorrectM(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IncorrectM)) {
            return false;
        }
        IncorrectM p = (IncorrectM) obj;
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
