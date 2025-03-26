package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Objects;

public final class FinalPointContainer {

    private final FinalPoint point;

    public FinalPointContainer(FinalPoint point) {
        this.point = point;
    }

    public FinalPoint getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FinalPointContainer)) {
            return false;
        }
        FinalPointContainer other = (FinalPointContainer) obj;
        return Objects.equals(point, other.point);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(point);
    }
}
