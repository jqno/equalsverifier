package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

import java.util.Objects;

public final class SubA2 extends SuperA {

    private final String s;

    public SubA2(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubA2)) {
            return false;
        }
        SubA2 other = (SubA2) obj;
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
