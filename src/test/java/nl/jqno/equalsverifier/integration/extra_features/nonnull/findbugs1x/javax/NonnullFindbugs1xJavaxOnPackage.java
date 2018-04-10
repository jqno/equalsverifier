package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public final class NonnullFindbugs1xJavaxOnPackage {
    private final Object o;

    public NonnullFindbugs1xJavaxOnPackage(Object o) { this.o = o; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonnullFindbugs1xJavaxOnPackage)) {
            return false;
        }
        NonnullFindbugs1xJavaxOnPackage other = (NonnullFindbugs1xJavaxOnPackage)obj;
        return o.equals(other.o);
    }

    @Override public int hashCode() { return defaultHashCode(this); }
}
