package nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.inapplicable;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullJsr305InapplicableOnPackage {
    private final Object o;

    public NonnullJsr305InapplicableOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullJsr305InapplicableOnPackage)) {
            return false;
        }
        NonnullJsr305InapplicableOnPackage other = (NonnullJsr305InapplicableOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return defaultHashCode(this);
    }
}
