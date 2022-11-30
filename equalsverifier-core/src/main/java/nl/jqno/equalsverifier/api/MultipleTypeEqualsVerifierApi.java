package nl.jqno.equalsverifier.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.util.ErrorMessage;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.util.ListBuilders;
import nl.jqno.equalsverifier.internal.util.ObjenesisWrapper;
import nl.jqno.equalsverifier.internal.util.Validations;

/**
 * Helps to construct an {@link EqualsVerifier} test for several types at once with a fluent API.
 */
public class MultipleTypeEqualsVerifierApi implements EqualsVerifierApi<Void> {

    private final List<Class<?>> types;
    private final ConfiguredEqualsVerifier ev;

    public MultipleTypeEqualsVerifierApi(List<Class<?>> types, ConfiguredEqualsVerifier ev) {
        this.types = new ArrayList<>(types);
        this.ev = ev.copy();
    }

    /** {@inheritDoc} */
    @Override
    public MultipleTypeEqualsVerifierApi suppress(Warning... warnings) {
        ev.suppress(warnings);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withPrefabValues(Class<S> otherType, S red, S blue) {
        ev.withPrefabValues(otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(
        Class<S> otherType,
        Func1<?, S> factory
    ) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(
        Class<S> otherType,
        Func2<?, ?, S> factory
    ) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MultipleTypeEqualsVerifierApi usingGetClass() {
        ev.usingGetClass();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MultipleTypeEqualsVerifierApi withResetCaches() {
        ObjenesisWrapper.reset();
        return this;
    }

    /**
     * Removes the given type or types from the list of types to verify.
     *
     * @param type A type to remove from the list of types to verify.
     * @param more More types to remove from the list of types to verify.
     * @return {@code this}, for easy method chaining.
     */
    public MultipleTypeEqualsVerifierApi except(Class<?> type, Class<?>... more) {
        List<Class<?>> typesToRemove = ListBuilders.buildListOfAtLeastOne(type, more);
        removeTypes(typesToRemove);
        return this;
    }

    /**
     * Removes all types matching the given Predicate.
     *
     * @param exclusionPredicate A Predicate matching classes to remove from the list of types to
     *     verify.
     * @return {@code this}, for easy method chaining.
     */
    public MultipleTypeEqualsVerifierApi except(Predicate<Class<?>> exclusionPredicate) {
        List<Class<?>> typesToRemove = types
            .stream()
            .filter(exclusionPredicate)
            .collect(Collectors.toList());
        removeTypes(typesToRemove);
        return this;
    }

    private void removeTypes(List<Class<?>> typesToRemove) {
        Validations.validateTypesAreKnown(typesToRemove, types);
        types.removeAll(typesToRemove);
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and throws
     * an {@link AssertionError} if there is a problem.
     *
     * @throws AssertionError If one of the contracts is not met, or if {@link EqualsVerifier}'s
     *     preconditions do not hold.
     */
    public void verify() {
        List<EqualsVerifierReport> failures = report()
            .stream()
            .filter(r -> !r.isSuccessful())
            .collect(Collectors.toList());
        if (failures.isEmpty()) {
            return;
        }
        String messages = Formatter
            .of(
                "EqualsVerifier found a problem in %% %%.\n---\n%%\n---\n%%\n%%",
                failures.size(),
                failures.size() == 1 ? "class" : "classes",
                failures
                    .stream()
                    .map(r -> "* " + r.getType().getName())
                    .collect(Collectors.joining("\n")),
                failures.stream().map(r -> r.getMessage()).collect(Collectors.joining("\n---\n")),
                ErrorMessage.suffix()
            )
            .format();
        throw new AssertionError(messages);
    }

    /**
     * Performs the verifications of the contracts for {@code equals} and {@code hashCode} and
     * returns a List of {@link EqualsVerifierReport} with the results of the verifications.
     *
     * @return A List of {@link EqualsVerifierReport} that indicates whether the contracts are met
     *     and whether {@link EqualsVerifier}'s preconditions hold.
     */
    public List<EqualsVerifierReport> report() {
        return types.stream().map(t -> ev.forClass(t).report(false)).collect(Collectors.toList());
    }
}
