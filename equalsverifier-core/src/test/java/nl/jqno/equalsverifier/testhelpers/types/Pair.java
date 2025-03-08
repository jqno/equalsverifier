package nl.jqno.equalsverifier.testhelpers.types;

import java.util.Objects;

public class Pair<T, U> {

    private final T left;
    private final U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        var other = (Pair<T, U>) obj;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": [" + left + ", " + right + "]";
    }
}
