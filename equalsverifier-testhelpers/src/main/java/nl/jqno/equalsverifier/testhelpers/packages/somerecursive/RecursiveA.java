package nl.jqno.equalsverifier.testhelpers.packages.somerecursive;

import java.util.Objects;

public final class RecursiveA {

    private final RecursiveB b;

    public RecursiveA(RecursiveB b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecursiveA)) {
            return false;
        }
        RecursiveA p = (RecursiveA) obj;
        return Objects.equals(b, p.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(b);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + b;
    }
}
