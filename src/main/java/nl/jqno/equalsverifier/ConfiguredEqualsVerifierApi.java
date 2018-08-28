package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;

import java.util.Collections;
import java.util.EnumSet;

public final class ConfiguredEqualsVerifierApi {
    private final EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private final FactoryCache factoryCache = new FactoryCache();
    private boolean usingGetClass = false;

    /**
     * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning}
     * to see what warnings can be suppressed.
     *
     * @param warnings A list of warnings to suppress in
     *          {@code EqualsVerifier}.
     * @return {@code this}, for easy method chaining.
     */
    public ConfiguredEqualsVerifierApi suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    /**
     * Adds prefabricated values for instance fields of classes that
     * EqualsVerifier cannot instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param red An instance of {@code S}.
     * @param black Another instance of {@code S}, not equal to {@code red}.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException If either {@code otherType}, {@code red},
     *          or {@code black} is null.
     * @throws IllegalArgumentException If {@code red} equals {@code black}.
     */
    public <S> ConfiguredEqualsVerifierApi withPrefabValues(Class<S> otherType, S red, S black) {
        PrefabValuesApi.addPrefabValues(factoryCache, otherType, red, black);
        return this;
    }

    /**
     * Adds a factory to generate prefabricated values for instance fields of
     * classes with 1 generic type parameter that EqualsVerifier cannot
     * instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory A factory to generate an instance of {@code S}, given a
     *          value of its generic type parameter.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or
     *          {@code factory} is null.
     */
    public <S> ConfiguredEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /**
     * Adds a factory to generate prefabricated values for instance fields of
     * classes with 2 generic type parameters that EqualsVerifier cannot
     * instantiate by itself.
     *
     * @param <S> The class of the prefabricated values.
     * @param otherType The class of the prefabricated values.
     * @param factory A factory to generate an instance of {@code S}, given a
     *          value of each of its generic type parameters.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException if either {@code otherType} or
     *          {@code factory} is null.
     */
    public <S> ConfiguredEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /**
     * Signals that {@code getClass} is used in the implementation of the
     * {@code equals} method, instead of an {@code instanceof} check.
     *
     * @return {@code this}, for easy method chaining.
     */
    public ConfiguredEqualsVerifierApi usingGetClass() {
        usingGetClass = true;
        return this;
    }

    /**
     * Factory method. For general use.
     *
     * @param type The class for which the {@code equals} method should be
     *          tested.
     * @return A fluent API for EqualsVerifier.
     */
    public <T> EqualsVerifierApi<T> forClass(Class<T> type) {
        return new EqualsVerifierApi<>(type, EnumSet.copyOf(warningsToSuppress), factoryCache, usingGetClass);
    }
}
