package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom;

import java.util.Objects;

public final class NonnullFindbugs1xWithCheckForNullOnPackage {

    private final Object o;

    @edu.umd.cs.findbugs.annotations.CheckForNull
    private final Object p;

    public NonnullFindbugs1xWithCheckForNullOnPackage(Object o, Object p) {
        this.o = o;
        this.p = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullFindbugs1xWithCheckForNullOnPackage)) {
            return false;
        }
        NonnullFindbugs1xWithCheckForNullOnPackage other = (NonnullFindbugs1xWithCheckForNullOnPackage) obj;
        return o.equals(other.o) && p.equals(other.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o, p);
    }
}
