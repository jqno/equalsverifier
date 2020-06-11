package nl.jqno.equalsverifier.internal.prefabvalues;

/**
 * Container for three values of the same type: a "red" one, a "blue" one, and a shallow copy of the
 * "red" one.
 */
public final class Tuple<T> {
    private final T red;
    private final T blue;
    private final T redCopy;

    /**
     * Constructor.
     *
     * @param red The red value.
     * @param blue The blue value.
     * @param redCopy A shallow copy of the red value.
     */
    public Tuple(T red, T blue, T redCopy) {
        this.red = red;
        this.blue = blue;
        this.redCopy = redCopy;
    }

    /**
     * Factory method that turns three untyped values into a typed tuple.
     *
     * @param red The red value.
     * @param blue The blue value.
     * @param redCopy A shallow copy of the red value.
     * @param <U> The assumed type of the values.
     * @return A typed tuple with the three given values.
     */
    @SuppressWarnings("unchecked")
    public static <U> Tuple<U> of(Object red, Object blue, Object redCopy) {
        return new Tuple<>((U) red, (U) blue, (U) redCopy);
    }

    /** @return The red value. */
    public T getRed() {
        return red;
    }

    /** @return The blue value. */
    public T getBlue() {
        return blue;
    }

    /** @return The shallow copy of the red value. */
    public T getRedCopy() {
        return redCopy;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple<?> other = (Tuple<?>) obj;
        return red.equals(other.red) && blue.equals(other.blue) && redCopy.equals(other.redCopy);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + red.hashCode();
        result = (59 * result) + blue.hashCode();
        result = (59 * result) + redCopy.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Tuple [" + red + ", " + blue + ", " + redCopy + "]";
    }
}
