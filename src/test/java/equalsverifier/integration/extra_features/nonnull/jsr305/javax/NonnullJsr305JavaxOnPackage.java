package equalsverifier.integration.extra_features.nonnull.jsr305.javax;

import static equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullJsr305JavaxOnPackage {
    private final Object o;

    public NonnullJsr305JavaxOnPackage(Object o) { this.o = o; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullJsr305JavaxOnPackage)) {
            return false;
        }
        NonnullJsr305JavaxOnPackage other = (NonnullJsr305JavaxOnPackage)obj;
        return o.equals(other.o);
    }

    @Override public int hashCode() { return defaultHashCode(this); }
}
