package nl.jqno.equalsverifier.integration.extra_features.simple_package.subpackage;

import java.util.Objects;
import nl.jqno.equalsverifier.testhelpers.types.Color;

public class SimplePoint {

    private int x;
    private int y;
    private Color color;

    public SimplePoint(int x) {
        this.x = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimplePoint that = (SimplePoint) o;
        return x == that.x && y == that.y && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }
}
