package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

public final class NonnullFindbugs1xCustomOnPackage {

    private final Object o;

    public NonnullFindbugs1xCustomOnPackage(Object o) {
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullFindbugs1xCustomOnPackage)) {
            return false;
        }
        NonnullFindbugs1xCustomOnPackage other = (NonnullFindbugs1xCustomOnPackage) obj;
        return o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return defaultHashCode(this);
    }
}
