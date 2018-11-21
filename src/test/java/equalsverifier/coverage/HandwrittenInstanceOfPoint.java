package equalsverifier.coverage;

import equalsverifier.testhelpers.types.Color;

public final class HandwrittenInstanceOfPoint {
    private final int x;
    private final int y;
    private final Color color;

    public HandwrittenInstanceOfPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HandwrittenInstanceOfPoint)) {
            return false;
        }
        HandwrittenInstanceOfPoint other = (HandwrittenInstanceOfPoint)obj;
        return x == other.x && y == other.y && (color == null ? other.color == null : color.equals(other.color));
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
