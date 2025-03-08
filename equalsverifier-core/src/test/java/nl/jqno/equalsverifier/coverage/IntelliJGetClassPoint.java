package nl.jqno.equalsverifier.coverage;

import nl.jqno.equalsverifier_testhelpers.types.Color;

/**
 * equals and hashCode generated by IntelliJ IDEA 12.
 *
 * <p>
 * Settings: - Accept subclasses as parameter to equals() method: false
 */
// CHECKSTYLE OFF: NeedBraces
public final class IntelliJGetClassPoint {

    private final int y;
    private final int x;
    private final Color color;

    public IntelliJGetClassPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IntelliJGetClassPoint point = (IntelliJGetClassPoint) o;

        if (x != point.x)
            return false;
        if (y != point.y)
            return false;
        if (color != point.color)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
