package nl.jqno.equalsverifier.testhelpers.packages.subclasses;

import java.util.Objects;

public final class SubI2 implements SuperI {

    private final String s;

    public SubI2(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubI2)) {
            return false;
        }
        SubI2 other = (SubI2) obj;
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
