package nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullJsr305WithCheckForNullOnPackage {
    private final Object o;
    @edu.umd.cs.findbugs.annotations.CheckForNull private final Object p;

    public NonnullJsr305WithCheckForNullOnPackage(Object o, Object p) {
        this.o = o;
        this.p = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullJsr305WithCheckForNullOnPackage)) {
            return false;
        }
        NonnullJsr305WithCheckForNullOnPackage other = (NonnullJsr305WithCheckForNullOnPackage) obj;
        return o.equals(other.o) && p.equals(other.p);
    }

    @Override
    public int hashCode() {
        return defaultHashCode(this);
    }
}
