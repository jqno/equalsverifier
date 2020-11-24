package nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.parametersarenonnullbydefault;

import java.util.Objects;

public final class ParametersAreNonnullByDefaultOnPackage {

    private final Object o;

    public ParametersAreNonnullByDefaultOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParametersAreNonnullByDefaultOnPackage)) {
            return false;
        }
        ParametersAreNonnullByDefaultOnPackage other = (ParametersAreNonnullByDefaultOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o);
    }
}
