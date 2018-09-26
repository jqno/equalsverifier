package nl.jqno.equalsverifier;

import java.util.List;

import static nl.jqno.equalsverifier.internal.util.ListBuilders.buildListOfAtLeastOne;
import static nl.jqno.equalsverifier.internal.util.ListBuilders.listContainsDuplicates;

/**
 * API class for
 * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
 * Its purpose is to make sure, at compile time, that a list of unequal
 * examples is given, as well as the list of equal examples that are
 * supplied to the aforementioned method.
 */
public class RelaxedEqualsVerifierApi<T> {
    private final Class<T> type;
    private final List<T> equalExamples;

    /**
     * Constructor, only to be called by
     * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
     */
    /* package protected */ RelaxedEqualsVerifierApi(Class<T> type, List<T> examples) {
        this.type = type;
        this.equalExamples = examples;
    }

    /**
     * Asks for an unequal instance of T and subsequently returns a fully
     * constructed instance of {@link EqualsVerifier}.
     *
     * @param example An instance of T that is unequal to the previously
     *          supplied equal examples.
     * @return An instance of {@link EqualsVerifier}.
     */
    public EqualsVerifierApi<T> andUnequalExample(T example) {
        return andUnequalExamples(example);
    }

    /**
     * Asks for a list of unequal instances of T and subsequently returns a
     * fully constructed instance of {@link EqualsVerifier}.
     *
     * @param first An instance of T that is unequal to the previously
     *          supplied equal examples.
     * @param more More instances of T, all of which are unequal to
     *          one another, to {@code first}, and to the previously
     *          supplied equal examples. May also contain instances of
     *          subclasses of T.
     * @return An instance of {@link EqualsVerifier}.
     */
    @SafeVarargs
    public final EqualsVerifierApi<T> andUnequalExamples(T first, T... more) {
        List<T> unequalExamples = buildListOfAtLeastOne(first, more);
        if (listContainsDuplicates(unequalExamples)) {
            throw new IllegalArgumentException("Two objects are equal to each other.");
        }
        for (T example : unequalExamples) {
            if (equalExamples.contains(example)) {
                throw new IllegalArgumentException("An equal example also appears as unequal example.");
            }
        }

        return new EqualsVerifierApi<>(type, equalExamples, unequalExamples)
            .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED);
    }
}
