package nl.jqno.equalsverifier.testhelpers.types;

public class BlindlyEqualsPoint {
    private final int x;
    private final int y;

    public BlindlyEqualsPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected boolean blindlyEquals(Object o) {
        if (!(o instanceof BlindlyEqualsPoint)) {
            return false;
        }
        BlindlyEqualsPoint p = (BlindlyEqualsPoint)o;
        return p.x == this.x && p.y == this.y;
    }

    @Override
    public boolean equals(Object o){
        return this.blindlyEquals(o) && ((BlindlyEqualsPoint)o).blindlyEquals(this);
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
