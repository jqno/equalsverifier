package equalsverifier.testhelpers.types;

import java.util.Objects;

public class PointContainer {
    private final Point point;

    public PointContainer(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointContainer)) {
            return false;
        }
        PointContainer other = (PointContainer)obj;
        return Objects.equals(point, other.point);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(point);
    }
}
