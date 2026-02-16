package nl.jqno.equalsverifier.jpms.inaccessible;

public final class JpmsInaccessible {
    private final int i;

    @SuppressWarnings("unused")
    private JpmsInaccessible(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JpmsInaccessible other && i == other.i;
    }

    @Override
    public int hashCode() {
        return i;
    }
}
