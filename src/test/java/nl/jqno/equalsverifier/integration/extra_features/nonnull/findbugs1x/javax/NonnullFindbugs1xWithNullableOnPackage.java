package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullFindbugs1xWithNullableOnPackage {
    private final Object o;
    @edu.umd.cs.findbugs.annotations.Nullable
    private final Object p;

    public NonnullFindbugs1xWithNullableOnPackage(Object o, Object p) { this.o = o; this.p = p; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullFindbugs1xWithNullableOnPackage)) {
            return false;
        }
        NonnullFindbugs1xWithNullableOnPackage other = (NonnullFindbugs1xWithNullableOnPackage)obj;
        return o.equals(other.o) && p.equals(other.p);
    }

    @Override public int hashCode() { return defaultHashCode(this); }
}
