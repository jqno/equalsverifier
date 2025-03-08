package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

public final class SubA1 extends SuperA {

    private final int i;

    public SubA1(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubA1)) {
            return false;
        }
        SubA1 other = (SubA1) obj;
        return i == other.i;
    }

    @Override
    public int hashCode() {
        return 31 * i;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + i;
    }
}
