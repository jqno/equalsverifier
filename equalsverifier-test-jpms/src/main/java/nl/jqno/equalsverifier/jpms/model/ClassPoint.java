package nl.jqno.equalsverifier.jpms.model;

import java.util.Objects;

public final class ClassPoint {

    private final int x;
    private final int y;

    public ClassPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClassPoint other && x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
