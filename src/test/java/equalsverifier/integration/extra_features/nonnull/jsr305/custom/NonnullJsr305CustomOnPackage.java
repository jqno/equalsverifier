package equalsverifier.integration.extra_features.nonnull.jsr305.custom;

import static equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullJsr305CustomOnPackage {
    private final Object o;

    public NonnullJsr305CustomOnPackage(Object o) { this.o = o; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullJsr305CustomOnPackage)) {
            return false;
        }
        NonnullJsr305CustomOnPackage other = (NonnullJsr305CustomOnPackage)obj;
        return o.equals(other.o);
    }

    @Override public int hashCode() { return defaultHashCode(this); }
}
