package nl.jqno.equalsverifier.internal.reflection;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Container for three values of the same type: a "red" one, a "blue" one, and a shallow copy of the "red" one.
 *
 * @param red     The red value.
 * @param blue    The blue value.
 * @param redCopy A shallow copy of the red value.
 * @param <T>     The assumed type of the values.
 */
public final record Tuple<T>(T red, T blue, T redCopy) {
    /**
     * Returns a new Tuple with the given function applied to each element.
     *
     * @param fn The function to apply to all values.
     * @return A new Tuple with the given function applied to each element.
     */
    public <U> Tuple<U> map(Function<T, U> fn) {
        return new Tuple<>(fn.apply(red), fn.apply(blue), fn.apply(redCopy));
    }

    /**
     * Returns a new Tuple with only the blue value switched to the given value and the other values left intact if the
     * red value equals the blue value; otherwise, returns the original tuple.
     *
     * This is useful for single-element enums, where only one value can be provided: in those cases, perhaps an
     * alternative can be provided (e.g. an empty list) using this method.
     *
     * @param newBlue A new value for blue.
     * @return A new tuple with the blue value switched if red an blue are equal, or the original tuple otherwise.
     */
    public Tuple<T> swapBlueIfEqualToRed(Supplier<T> newBlue) {
        if (red.equals(blue)) {
            return new Tuple<>(red, newBlue.get(), redCopy);
        }
        return this;
    }
}
