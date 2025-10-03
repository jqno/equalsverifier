package nl.jqno.equalsverifier.internal.reflection;

import java.util.function.Function;

/**
 * Container for three values of the same type: a "red" one, a "blue" one, and a shallow copy of the "red" one.
 *
 * @param red     The red value.
 * @param blue    The blue value.
 * @param redCopy A shallow copy of the red value.
 * @param <T>     The assumed type of the values.
 */
public final record Tuple<T>(T red, T blue, T redCopy) {
    public <U> Tuple<U> map(Function<T, U> fn) {
        return new Tuple<>(fn.apply(red), fn.apply(blue), fn.apply(redCopy));
    }
}
