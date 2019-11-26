package nl.jqno.equalsverifier.testhelpers.types;

import java.util.Objects;

public final class BlindlyEqualsColorPoint extends BlindlyEqualsPoint {
    private final Color color;

    public BlindlyEqualsColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    protected boolean blindlyEquals(Object o) {
        if (!(o instanceof BlindlyEqualsColorPoint)) {
            return false;
        }
        BlindlyEqualsColorPoint cp = (BlindlyEqualsColorPoint) o;
        return super.blindlyEquals(cp) && cp.color == this.color;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(color) + (31 * super.hashCode());
    }

    @Override
    public String toString() {
        return super.toString() + "," + color;
    }
}
