package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Objects;

public final class PointContainerOpenForSubclassAttack {

    private final Point point;

    public PointContainerOpenForSubclassAttack(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointContainerOpenForSubclassAttack)) {
            return false;
        }
        var other = (PointContainerOpenForSubclassAttack) obj;
        return Objects.equals(point, other.point);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(point);
    }
}
