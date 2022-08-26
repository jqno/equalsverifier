package nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

public final class NonnullJsr305WithNullableOnPackage {

    private final Object o;

    @edu.umd.cs.findbugs.annotations.Nullable
    private final Object p;

    public NonnullJsr305WithNullableOnPackage(Object o, Object p) {
        this.o = o;
        this.p = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullJsr305WithNullableOnPackage)) {
            return false;
        }
        NonnullJsr305WithNullableOnPackage other = (NonnullJsr305WithNullableOnPackage) obj;
        return o.equals(other.o) && p.equals(other.p);
    }

    @Override
    public int hashCode() {
        return defaultHashCode(this);
    }
}
