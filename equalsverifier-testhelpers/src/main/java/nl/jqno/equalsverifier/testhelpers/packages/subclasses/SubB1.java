package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

public final class SubB1 extends SuperB {

    private final int i;

    public SubB1(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubB1)) {
            return false;
        }
        SubB1 other = (SubB1) obj;
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
