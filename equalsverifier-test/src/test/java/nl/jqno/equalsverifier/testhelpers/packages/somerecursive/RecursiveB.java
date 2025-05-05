package nl.jqno.equalsverifier.testhelpers.packages.somerecursive;

import java.util.Objects;

public final class RecursiveB {

    private final RecursiveA a;

    public RecursiveB(RecursiveA a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecursiveB)) {
            return false;
        }
        RecursiveB p = (RecursiveB) obj;
        return Objects.equals(a, p.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + a;
    }
}
