package nl.jqno.equalsverifier.jpms.model;

public class NonFinal {
    private final int i;

    public NonFinal(int i) {
        this.i = i;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof NonFinal other && i == other.i;
    }

    @Override
    public final int hashCode() {
        return Integer.hashCode(i);
    }
}
