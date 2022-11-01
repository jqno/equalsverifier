package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

import java.util.Objects;

public final class SubB2 extends SuperB {

    private final String s;

    public SubB2(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubB2)) {
            return false;
        }
        SubB2 other = (SubB2) obj;
        return Objects.equals(s, other.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + s;
    }
}
