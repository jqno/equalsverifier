package nl.jqno.equalsverifier.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.*;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.util.ErrorMessage;
import nl.jqno.equalsverifier.internal.util.Formatter;

/**
 * Helps to construct an {@link EqualsVerifier} test for several types at once with a fluent API.
 *
 * @since 3.2
 */
public class MultipleTypeEqualsVerifierApi implements EqualsVerifierApi<Void> {

    private final List<Class<?>> types;
    private final ConfiguredEqualsVerifier ev;

    /**
     * Constructor.
     *
     * @param types The classes for which the {@code equals} method should be tested.
     * @param ev    A configuration that can be used for each class.
     *
     * @since 3.2
     */
    public MultipleTypeEqualsVerifierApi(List<Class<?>> types, ConfiguredEqualsVerifier ev) {
        this.types = new ArrayList<>(types);
        this.ev = ev.copy();
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public MultipleTypeEqualsVerifierApi suppress(Warning... warnings) {
        ev.suppress(warnings);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public MultipleTypeEqualsVerifierApi set(Mode... modes) {
        ev.set(modes);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public <S> MultipleTypeEqualsVerifierApi withPrefabValues(Class<S> otherType, S red, S blue) {
        ev.withPrefabValues(otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public <S> MultipleTypeEqualsVerifierApi withPrefabValueSupplier(
            Class<S> otherType,
            Supplier<S> red,
            Supplier<S> blue) {
        ev.withPrefabValueSupplier(otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public MultipleTypeEqualsVerifierApi usingGetClass() {
        ev.usingGetClass();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    @SuppressWarnings("CheckReturnValue")
    public MultipleTypeEqualsVerifierApi withFieldnameToGetterConverter(Function<String, String> converter) {
        ev.withFieldnameToGetterConverter(converter);
        return this;
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and throws an
     * {@link AssertionError} if there is a problem.
     *
     * @throws AssertionError If one of the contracts is not met, or if {@link EqualsVerifier}'s preconditions do not
     *                            hold.
     *
     * @since 0.1
     */
    public void verify() {
        List<EqualsVerifierReport> failures = report().stream().filter(r -> !r.isSuccessful()).toList();
        if (failures.isEmpty()) {
            return;
        }
        String messages = Formatter
                .of(
                    "EqualsVerifier found a problem in %% %%.\n---\n%%\n---\n%%\n---\n%%",
                    failures.size(),
                    failures.size() == 1 ? "class" : "classes",
                    failures.stream().map(r -> "* " + r.getType().getName()).collect(Collectors.joining("\n")),
                    failures.stream().map(r -> r.getMessage()).collect(Collectors.joining("\n---\n")),
                    ErrorMessage.suffix())
                .format();
        throw new AssertionError(messages);
    }

    /**
     * Performs the verifications of the contracts for {@code equals} and {@code hashCode} and returns a List of
     * {@link EqualsVerifierReport} with the results of the verifications.
     *
     * @return A List of {@link EqualsVerifierReport} that indicates whether the contracts are met and whether
     *             {@link EqualsVerifier}'s preconditions hold.
     *
     * @since 3.0
     */
    public List<EqualsVerifierReport> report() {
        return types.stream().map(t -> ev.forClass(t).report(false)).toList();
    }
}
