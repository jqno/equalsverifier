package nl.jqno.equalsverifier.integration.extra_features.nonnull.jspecify;

import java.util.Objects;

public final class NullMarkedOnPackage {

    private final Object o;

    public NullMarkedOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NullMarkedOnPackage)) {
            return false;
        }
        NullMarkedOnPackage other = (NullMarkedOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o);
    }
}
