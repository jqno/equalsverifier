package equalsverifier.testhelpers.types;

public class FinalMethodsPoint {
    private final int x;
    private final int y;

    public FinalMethodsPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof FinalMethodsPoint)) {
            return false;
        }
        FinalMethodsPoint p = (FinalMethodsPoint)obj;
        return p.x == x && p.y == y;
    }

    @Override
    public final int hashCode() {
        return x + (31 * y);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + x + "," + y;
    }
}
