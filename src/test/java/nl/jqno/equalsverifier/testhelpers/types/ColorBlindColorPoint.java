package nl.jqno.equalsverifier.testhelpers.types;

public final class ColorBlindColorPoint extends Point {

    public final Color color;

    public ColorBlindColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + "," + color;
    }
}
