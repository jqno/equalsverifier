package nl.jqno.equalsverifier.api;

import java.util.function.Function;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;

/**
 * Helps to construct an {@link EqualsVerifier} test with a fluent API.
 *
 * @param <T> The class under test.
 *
 * @since 3.2
 */
public interface EqualsVerifierApi<T> {
    /**
     * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning} to see what warnings can be suppressed.
     *
     * @param warnings A list of warnings to suppress in {@code EqualsVerifier}.
     * @return {@code this}, for easy method chaining.
     *
     * @since 0.6
     */
    EqualsVerifierApi<T> suppress(Warning... warnings);

    /**
     * Sets modes that influence how {@code EqualsVerifier} operates.
     *
     * @param modes A list of modes to influence how {@code EqualsVerifier} operates.
     * @return {@code this}, for easy method chaining.
     *
     * @since 4.0
     */
    EqualsVerifierApi<T> set(Mode... modes);

    /**
     * Adds prefabricated values for instance fields of classes that EqualsVerifier cannot instantiate by itself.
     *
     * @param <S>       The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param red       An instance of {@code S}.
     * @param blue      Another instance of {@code S}, not equal to {@code red}.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException     If either {@code otherType}, {@code red}, or {@code blue} is null.
     * @throws IllegalArgumentException If {@code red} equals {@code blue}.
     *
     * @since 0.2
     */
    <S> EqualsVerifierApi<T> withPrefabValues(Class<S> otherType, S red, S blue);

    /**
     * Adds Suppliers for resettable prefabricated values for instance fields of classes that EqualsVerifier cannot
     * instantiate by itself. Resettable values are values that are created new every time a new instance of {@code T}
     * is constructed within EqualsVerifier. This can be useful when {@code T} mutates its instances of {@code S}.
     *
     * @param <S>       The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param red       An instance of {@code S}.
     * @param blue      Another instance of {@code S}, not equal to {@code red}.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException     If either {@code otherType}, {@code red}, or {@code blue} is null.
     * @throws IllegalArgumentException If {@code red} equals {@code blue}.
     *
     * @since 4.1
     */
    <S> EqualsVerifierApi<T> withResettablePrefabValues(Class<S> otherType, Supplier<S> red, Supplier<S> blue);

    /**
     * Adds a factory to generate prefabricated values for instance fields of classes with 1 generic type parameter that
     * EqualsVerifier cannot instantiate by itself.
     *
     * @param <S>       The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory   A factory to generate an instance of {@code S}, given a value of its generic type parameter.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or {@code factory} is null.
     *
     * @since 3.0
     */
    <S> EqualsVerifierApi<T> withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory);

    /**
     * Adds a factory to generate prefabricated values for instance fields of classes with 2 generic type parameters
     * that EqualsVerifier cannot instantiate by itself.
     *
     * @param <S>       The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory   A factory to generate an instance of {@code S}, given a value of each of its generic type
     *                      parameters.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or {@code factory} is null.
     *
     * @since 3.0
     */
    <S> EqualsVerifierApi<T> withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory);

    /**
     * Signals that {@code getClass} is used in the implementation of the {@code equals} method, instead of an
     * {@code instanceof} check.
     *
     * @return {@code this}, for easy method chaining.
     * @see Warning#STRICT_INHERITANCE
     *
     * @since 0.7
     */
    EqualsVerifierApi<T> usingGetClass();

    /**
     * Determines how a getter name can be derived from a field name.
     *
     * The default behavior is to uppercase the field's first letter and prepend 'get'. For instance, a field name
     * 'employee' would correspond to getter name 'getEmployee'.
     *
     * This method can be used if your project has a different naming convention.
     *
     * @param converter A function that converts from field name to getter name.
     * @return {@code this}, for easy method chaining.
     *
     * @since 3.15
     */
    EqualsVerifierApi<T> withFieldnameToGetterConverter(Function<String, String> converter);
}
