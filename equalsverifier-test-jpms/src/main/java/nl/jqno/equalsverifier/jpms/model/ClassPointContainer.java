package nl.jqno.equalsverifier.jpms.model;

import java.util.Objects;

public final class ClassPointContainer {
    private final ClassPoint cp;

    private ClassPointContainer(ClassPoint cp) {
        this.cp = cp;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClassPointContainer other && Objects.equals(cp, other.cp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cp);
    }
}
