package nl.jqno.equalsverifier;

import java.util.List;
import nl.jqno.equalsverifier.api.MultipleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.api.RelaxedEqualsVerifierApi;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.internal.reflection.PackageScanner;
import nl.jqno.equalsverifier.internal.util.ListBuilders;
import nl.jqno.equalsverifier.internal.util.Validations;

/**
 * {@code EqualsVerifier} can be used in unit tests to verify whether the contract for the {@code
 * equals} and {@code hashCode} methods in a class is met.
 *
 * <p>The contracts are described in the Javadoc comments for {@link
 * java.lang.Object#equals(Object)} and {@link java.lang.Object#hashCode()}
 *
 * <p>To get started, use {@code EqualsVerifier} as follows:
 *
 * <p>{@code EqualsVerifier.forClass(My.class).verify();}
 *
 * <p>For more information, see the documentation at http://www.jqno.nl/equalsverifier
 */
public final class EqualsVerifier {

    /**
     * Private constructor. Call {@link #forClass(Class)} or {@link #forRelaxedEqualExamples(Object,
     * Object, Object...)} instead.
     */
    private EqualsVerifier() {}

    /**
     * Creates a configuration object that can be reused with EqualsVerifier for multiple classes.
     * It has a fluent API.
     *
     * <p>Save the configuration in a variable of type {@link EqualsVerifierApi} and call {@link
     * #forClass(Class)} on it for each class whose {@code equals} and {@code hashCode} must be
     * verified.
     *
     * @return A reusable configuration object with a fluent API.
     */
    public static ConfiguredEqualsVerifier configure() {
        return new ConfiguredEqualsVerifier();
    }

    /**
     * Factory method. For general use.
     *
     * @param type The class for which the {@code equals} method should be tested.
     * @param <T> The type.
     * @return A fluent API for EqualsVerifier.
     */
    public static <T> SingleTypeEqualsVerifierApi<T> forClass(Class<T> type) {
        return new SingleTypeEqualsVerifierApi<>(type);
    }

    /**
     * Factory method. For general use.
     *
     * @param first A class for which the {@code equals} method should be tested.
     * @param second Another class for which the {@code equals} method should be tested.
     * @param more More classes for which the {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public static MultipleTypeEqualsVerifierApi forClasses(
            Class<?> first, Class<?> second, Class<?>... more) {
        return new MultipleTypeEqualsVerifierApi(
                ListBuilders.buildListOfAtLeastTwo(first, second, more),
                new ConfiguredEqualsVerifier());
    }

    /**
     * Factory method. For general use.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public static MultipleTypeEqualsVerifierApi forPackage(String packageName) {
        List<Class<?>> classes = PackageScanner.getClassesIn(packageName);
        Validations.validatePackageContainsClasses(packageName, classes);
        return new MultipleTypeEqualsVerifierApi(classes, new ConfiguredEqualsVerifier());
    }

    /**
     * Factory method. Asks for a list of equal, but not identical, instances of T.
     *
     * <p>For use when T is a class which has relaxed equality rules. This happens when two
     * instances of T are equal even though the its internal state is different.
     *
     * <p>This could happen, for example, in a Rational class that doesn't normalize: new
     * Rational(1, 2).equals(new Rational(2, 4)) would return true.
     *
     * <p>Using this factory method requires that {@link
     * RelaxedEqualsVerifierApi#andUnequalExamples(Object, Object...)} be called to supply a list of
     * unequal instances of T.
     *
     * <p>This method automatically suppresses {@link Warning#ALL_FIELDS_SHOULD_BE_USED}.
     *
     * @param first An instance of T.
     * @param second Another instance of T, which is equal, but not identical, to {@code first}.
     * @param more More instances of T, all of which are equal, but not identical, to one another
     *     and to {@code first} and {@code second}.
     * @param <T> the type.
     * @return A fluent API for a more relaxed EqualsVerifier.
     */
    @SafeVarargs
    public static <T> RelaxedEqualsVerifierApi<T> forRelaxedEqualExamples(
            T first, T second, T... more) {
        List<T> examples = ListBuilders.buildListOfAtLeastTwo(first, second, more);

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) first.getClass();

        return new RelaxedEqualsVerifierApi<>(type, examples);
    }
}
