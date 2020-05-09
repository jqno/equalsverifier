package nl.jqno.equalsverifier.api;

import static nl.jqno.equalsverifier.internal.util.ListBuilders.buildListOfAtLeastOne;

import java.util.List;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.util.Validations;

/**
 * API class for {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}. Its
 * purpose is to make sure, at compile time, that a list of unequal examples is given, as well as
 * the list of equal examples that are supplied to the aforementioned method.
 */
public class RelaxedEqualsVerifierApi<T> {
    private final Class<T> type;
    private final List<T> equalExamples;

    /**
     * Constructor.
     *
     * @param type The class for which the {@code equals} method should be tested.
     * @param examples A list of example instances that are equal but not identical to one another.
     */
    public RelaxedEqualsVerifierApi(Class<T> type, List<T> examples) {
        this.type = type;
        this.equalExamples = examples;
    }

    /**
     * Asks for an unequal instance of T and subsequently returns a fully constructed instance of
     * {@link EqualsVerifier}.
     *
     * @param example An instance of T that is unequal to the previously supplied equal examples.
     * @return An instance of {@link EqualsVerifier}.
     */
    public SingleTypeEqualsVerifierApi<T> andUnequalExample(T example) {
        return andUnequalExamples(example);
    }

    /**
     * Asks for a list of unequal instances of T and subsequently returns a fully constructed
     * instance of {@link EqualsVerifier}.
     *
     * @param first An instance of T that is unequal to the previously supplied equal examples.
     * @param more More instances of T, all of which are unequal to one another, to {@code first},
     *     and to the previously supplied equal examples. May also contain instances of subclasses
     *     of T.
     * @return An instance of {@link EqualsVerifier}.
     */
    @SafeVarargs
    public final SingleTypeEqualsVerifierApi<T> andUnequalExamples(T first, T... more) {
        List<T> unequalExamples = buildListOfAtLeastOne(first, more);
        Validations.validateUnequalExamples(unequalExamples, equalExamples);
        return new SingleTypeEqualsVerifierApi<>(type, equalExamples, unequalExamples)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED);
    }
}
