package nl.jqno.equalsverifier.integration.extra_features.simple_package.subpackage;

import java.util.Objects;
import nl.jqno.equalsverifier.testhelpers.types.Color;

public class IntelliJPoint2 {

    private int x;
    private int y;
    private Color color;

    public IntelliJPoint2(int x) {
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
        IntelliJPoint2 that = (IntelliJPoint2) o;
        return x == that.x && y == that.y && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }
}
