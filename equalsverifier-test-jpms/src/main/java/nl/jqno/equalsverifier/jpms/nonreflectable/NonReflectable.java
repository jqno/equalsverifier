package nl.jqno.equalsverifier.jpms.nonreflectable;

public final class NonReflectable {
    private final int i;

    private NonReflectable(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NonReflectable other && i == other.i;
    }

    @Override
    public int hashCode() {
        return i;
    }
}
