package nl.jqno.equalsverifier.testhelpers.types;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class Pair<T, U> {

    private final T left;
    private final U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        return defaultEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return defaultHashCode(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": [" + left + ", " + right + "]";
    }
}
