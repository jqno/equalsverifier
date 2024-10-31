package nl.jqno.equalsverifier.internal.reflection;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public <R> Tuple<R> map(Function<T, R> fn) {
        return Tuple.of(fn.apply(red), fn.apply(blue), fn.apply(redCopy));
    }

    public static <T, U, R> Tuple<R> combine(Tuple<T> t, Tuple<U> u, BiFunction<T, U, R> fn) {
        return Tuple.of(
            fn.apply(t.getRed(), u.getRed()),
            fn.apply(t.getBlue(), u.getBlue()),
            fn.apply(t.getRedCopy(), u.getRedCopy())
        );
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple<?> other = (Tuple<?>) obj;
        return (
            Objects.equals(red, other.red) &&
            Objects.equals(blue, other.blue) &&
            Objects.equals(redCopy, other.redCopy)
        );
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(red, blue, redCopy);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Tuple [" + red + ", " + blue + ", " + redCopy + "]";
    }
}
