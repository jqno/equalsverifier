package nl.jqno.equalsverifier.coverage;

import nl.jqno.equalsverifier_testhelpers.types.Color;

public final class PatternMatchInstanceofPoint {

    private final int x;
    private final int y;
    private final Color color;

    public PatternMatchInstanceofPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PatternMatchInstanceofPoint other
                && x == other.x
                && y == other.y
                && (color == null ? other.color == null : color.equals(other.color));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (color == null ? 0 : color.hashCode());
        return result;
    }
}
