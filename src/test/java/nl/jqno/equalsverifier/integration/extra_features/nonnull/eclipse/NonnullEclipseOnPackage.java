package nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse;

import java.util.Objects;

public final class NonnullEclipseOnPackage {
    private final Object o;

    public NonnullEclipseOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullEclipseOnPackage)) {
            return false;
        }
        NonnullEclipseOnPackage other = (NonnullEclipseOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o);
    }
}
