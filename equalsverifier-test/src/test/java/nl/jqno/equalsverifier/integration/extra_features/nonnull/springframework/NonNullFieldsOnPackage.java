package nl.jqno.equalsverifier.integration.extra_features.nonnull.springframework;

import java.util.Objects;

public final class NonNullFieldsOnPackage {

    private final Object o;

    public NonNullFieldsOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonNullFieldsOnPackage)) {
            return false;
        }
        NonNullFieldsOnPackage other = (NonNullFieldsOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o);
    }
}
