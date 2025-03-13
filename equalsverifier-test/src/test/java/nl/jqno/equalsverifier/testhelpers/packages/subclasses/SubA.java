package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

public final class SubA extends SuperA {

    private final int i;

    public SubA(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubA)) {
            return false;
        }
        SubA other = (SubA) obj;
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
