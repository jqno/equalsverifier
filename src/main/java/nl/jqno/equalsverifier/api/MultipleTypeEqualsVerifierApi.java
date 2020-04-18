package nl.jqno.equalsverifier.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.util.Formatter;

/**
 * Helps to construct an {@link EqualsVerifier} test for several types at once with a fluent API.
 *
 * @param <T> The class under test.
 */
public class MultipleTypeEqualsVerifierApi implements EqualsVerifierApi<Void> {

    private final List<Class<?>> types;
    private final ConfiguredEqualsVerifier ev = new ConfiguredEqualsVerifier();

    public MultipleTypeEqualsVerifierApi(List<Class<?>> types) {
        this.types = new ArrayList<>(types);
    }

    /** {@inheritDoc} */
    @Override
    public MultipleTypeEqualsVerifierApi suppress(Warning... warnings) {
        ev.suppress(warnings);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withPrefabValues(Class<S> otherType, S red, S black) {
        ev.withPrefabValues(otherType, red, black);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(
            Class<S> otherType, Func1<?, S> factory) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> MultipleTypeEqualsVerifierApi withGenericPrefabValues(
            Class<S> otherType, Func2<?, ?, S> factory) {
        ev.withGenericPrefabValues(otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public MultipleTypeEqualsVerifierApi usingGetClass() {
        ev.usingGetClass();
        return this;
    }

    public void verify() {
        List<EqualsVerifierReport> failures =
                report().stream().filter(r -> !r.isSuccessful()).collect(Collectors.toList());
        if (failures.isEmpty()) {
            return;
        }
        String messages =
                Formatter.of(
                                "EqualsVerifier found a problem in %% %%.\n---\n%%\n---\n%%",
                                failures.size(),
                                failures.size() == 1 ? "class" : "classes",
                                failures.stream()
                                        .map(r -> "* " + r.getType().getName())
                                        .collect(Collectors.joining("\n")),
                                failures.stream()
                                        .map(r -> r.getMessage())
                                        .collect(Collectors.joining("\n---\n")))
                        .format();
        throw new AssertionError(messages);
    }

    public List<EqualsVerifierReport> report() {
        return types.stream().map(t -> ev.forClass(t).report()).collect(Collectors.toList());
    }
}
