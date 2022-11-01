package nl.jqno.equalsverifier.testhelpers.packages.subclasses.subpackage;

import java.util.Objects;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperA;

public final class SubA3 extends SuperA {

    private final String s;

    public SubA3(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubA3)) {
            return false;
        }
        SubA3 other = (SubA3) obj;
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
