package nl.jqno.equalsverifier.api;

import java.util.*;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.*;
import nl.jqno.equalsverifier.internal.util.*;

/**
 * Helps to construct an {@link EqualsVerifier} test with a fluent API.
 *
 * @param <T> The class under test.
 */
public interface EqualsVerifierApi<T> {

    /**
     * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning} to see what warnings
     * can be suppressed.
     *
     * @param warnings A list of warnings to suppress in {@code EqualsVerifier}.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> suppress(Warning... warnings);

    /**
     * Adds prefabricated values for instance fields of classes that EqualsVerifier cannot
     * instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param red An instance of {@code S}.
     * @param black Another instance of {@code S}, not equal to {@code red}.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException If either {@code otherType}, {@code red}, or {@code black} is
     *     null.
     * @throws IllegalArgumentException If {@code red} equals {@code black}.
     */
    public <S> EqualsVerifierApi<T> withPrefabValues(Class<S> otherType, S red, S black);

    /**
     * Adds a factory to generate prefabricated values for instance fields of classes with 1 generic
     * type parameter that EqualsVerifier cannot instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory A factory to generate an instance of {@code S}, given a value of its generic
     *     type parameter.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or {@code factory} is null.
     */
    public <S> EqualsVerifierApi<T> withGenericPrefabValues(
            Class<S> otherType, Func1<?, S> factory);

    /**
     * Adds a factory to generate prefabricated values for instance fields of classes with 2 generic
     * type parameters that EqualsVerifier cannot instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory A factory to generate an instance of {@code S}, given a value of each of its
     *     generic type parameters.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or {@code factory} is null.
     */
    public <S> EqualsVerifierApi<T> withGenericPrefabValues(
            Class<S> otherType, Func2<?, ?, S> factory);

    /**
     * Signals that {@code getClass} is used in the implementation of the {@code equals} method,
     * instead of an {@code instanceof} check.
     *
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> usingGetClass();
}
