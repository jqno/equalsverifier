package nl.jqno.equalsverifier.internal.prefabvalues;

/**
 * Container for three values of the same type: a "red" one, a "black" one, and a shallow copy of the "red" one.
 */
public final class Tuple<T> {
    private final T red;
    private final T black;
    private final T redCopy;

    /**
     * Constructor.
     *
     * @param red The red value.
     * @param black The black value.
     * @param redCopy A shallow copy of the red value.
     */
    public Tuple(T red, T black, T redCopy) {
        this.red = red;
        this.black = black;
        this.redCopy = redCopy;
    }

    /**
     * Factory method that turns three untyped values into a typed tuple.
     *
     * @param red The red value.
     * @param black The black value.
     * @param redCopy A shallow copy of the red value.
     * @param <U> The assumed type of the values.
     */
    @SuppressWarnings("unchecked")
    public static <U> Tuple<U> of(Object red, Object black, Object redCopy) {
        return new Tuple<>((U)red, (U)black, (U)redCopy);
    }

    /**
     * Returns the red value.
     */
    public T getRed() {
        return red;
    }

    /**
     * Returns the black value.
     */
    public T getBlack() {
        return black;
    }

    /**
     * Returns the shallow copy of the red value.
     */
    public T getRedCopy() {
        return redCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple<?> other = (Tuple<?>)obj;
        return red.equals(other.red) && black.equals(other.black) && redCopy.equals(other.redCopy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + red.hashCode();
        result = (59 * result) + black.hashCode();
        result = (59 * result) + redCopy.hashCode();
        return result;
    }
}
