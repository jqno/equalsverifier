package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.checkers.*;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;
import org.objectweb.asm.Type;

import java.util.*;

/**
 * {@code EqualsVerifier} can be used in unit tests to verify whether the
 * contract for the {@code equals} and {@code hashCode} methods in a class is
 * met.
 * <p>
 * The contracts are described in the Javadoc comments for
 * {@link java.lang.Object#equals(Object)} and
 * {@link java.lang.Object#hashCode()}
 * <p>
 * To get started, use {@code EqualsVerifier} as follows:
 * <p>
 * {@code EqualsVerifier.forClass(My.class).verify();}
 * <p>
 * For more information, see the documentation at
 * http://www.jqno.nl/equalsverifier
 *
 * @param <T> The class under test.
 */
public final class EqualsVerifier<T> {
    private Configuration<T> config;

    /**
     * Private constructor. Call {@link #forClass(Class)} or
     * {@link #forRelaxedEqualExamples(Object, Object, Object...)} instead.
     */
    private EqualsVerifier(Configuration<T> config) {
        this.config = config;
        JavaApiPrefabValues.addTo(config.getPrefabValues());
    }

    /**
     * Factory method. For general use.
     *
     * @param type The class for which the {@code equals} method should be
     *          tested.
     */
    public static <T> EqualsVerifier<T> forClass(Class<T> type) {
        Configuration<T> config = Configuration.of(type);
        return new EqualsVerifier<>(config);
    }

    /**
     * Factory method. Asks for a list of equal, but not identical, instances
     * of T.
     *
     * For use when T is a class which has relaxed equality
     * rules. This happens when two instances of T are equal even though the
     * its internal state is different.
     *
     * This could happen, for example, in a Rational class that doesn't
     * normalize: new Rational(1, 2).equals(new Rational(2, 4)) would return
     * true.
     *
     * Using this factory method requires that
     * {@link RelaxedEqualsVerifierHelper#andUnequalExamples(Object, Object...)}
     * be called to supply a list of unequal instances of T.
     *
     * This method automatically suppresses
     * {@link Warning#ALL_FIELDS_SHOULD_BE_USED}.
     *
     * @param first An instance of T.
     * @param second Another instance of T, which is equal, but not identical,
     *          to {@code first}.
     * @param more More instances of T, all of which are equal, but not
     *          identical, to one another and to {@code first} and
     *          {@code second}.
     */
    @SafeVarargs
    public static <T> RelaxedEqualsVerifierHelper<T> forRelaxedEqualExamples(T first, T second, T... more) {
        List<T> examples = buildListOfAtLeastTwo(first, second, more);

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)first.getClass();

        return new RelaxedEqualsVerifierHelper<>(type, examples);
    }

    /**
     * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning}
     * to see what warnings can be suppressed.
     *
     * @param warnings A list of warnings to suppress in
     *          {@code EqualsVerifier}.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> suppress(Warning... warnings) {
        EnumSet<Warning> ws = config.getWarningsToSuppress();
        Collections.addAll(ws, warnings);
        config = config.withWarningsToSuppress(ws);
        assertNoNonnullFields();
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
    public <S> EqualsVerifier<T> withPrefabValues(Class<S> otherType, S red, S black) {
        if (otherType == null) {
            throw new NullPointerException("Type is null");
        }
        if (red == null || black == null) {
            throw new NullPointerException(String.format( "Red: %s or Black: %s is null", red, black ));
        }
        if (red.equals(black)) {
            throw new IllegalArgumentException("Both values are equal.");
        }

        if (red.getClass().isArray()) {
            config.getPrefabValues().addFactory(otherType, red, black, red);
        }
        else {
            S redCopy = ObjectAccessor.of(red).copy();
            config.getPrefabValues().addFactory(otherType, red, black, redCopy);
        }
        return this;
    }

    /**
     * Signals that {@code getClass} is used in the implementation of the
     * {@code equals} method, instead of an {@code instanceof} check.
     *
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> usingGetClass() {
        config = config.withUsingGetClass();
        return this;
    }

    /**
     * Signals that all given fields are not relevant for the {@code equals}
     * contract. {@code EqualsVerifier} will not fail if one of these fields
     * does not affect the outcome of {@code equals}, but it will fail if one
     * of these fields does affect the outcome of {@code equals}.
     *
     * Note that these fields will still be used to test for null-ness, among
     * other things.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withIgnoredFields(String... fields) {
        assertNoExistingIncludedFields();
        List<String> toBeExcludedFields = Arrays.asList(fields);
        validateFieldNamesExist(toBeExcludedFields);

        List<String> allExcludedFields = new ArrayList<>(config.getExcludedFields());
        allExcludedFields.addAll(toBeExcludedFields);
        config = config.withExcludedFields(allExcludedFields);
        return this;
    }

    /**
     * Signals that all given fields, and <em>only</em> the given fields, are
     * relevant for the {@code equals} contract. {@code EqualsVerifier} will
     * fail if one of these fields does not affect the outcome of
     * {@code equals}, and it will fail if a field that isn't listed here, does
     * affect the outcome of {@code equals}.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withOnlyTheseFields(String... fields) {
        assertNoExistingExcludedFields();
        List<String> specifiedFields = Arrays.asList(fields);

        validateFieldNamesExist(specifiedFields);

        List<String> allIncludedFields = new ArrayList<>(config.getIncludedFields());
        allIncludedFields.addAll(specifiedFields);
        config = config.withIncludedFields(allIncludedFields);
        return this;
    }

    /**
     * Signals that all given fields can never be null, and {@code
     * EqualsVerifier} therefore doesn't have to verify that proper null checks
     * are in place for these fields.
     *
     * This can be used instead of {link #suppress(Warning...)}, which provides
     * the same behaviour for all fields, when only some fields are never null
     * but others are.
     *
     * @param fields Fields that can never be null.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withNonnullFields(String... fields) {
        List<String> nonnullFields = Arrays.asList(fields);
        validateFieldNamesExist(nonnullFields);
        config = config.withNonnullFields(nonnullFields);
        assertNoNonnullFields();
        return this;
    }

    /**
     * Signals that all given annotations are to be ignored by EqualsVerifier.
     *
     * For instance, EqualsVerifier normally doesn't perform null verifications
     * on fields marked with an {@code @Nonnull} annotation. However, if this
     * method is called with a {@code Nonnull.class} parameter, the null
     * verifications will be performed after all.
     *
     * @param annotations Annotations to ignore.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withIgnoredAnnotations(Class<?>... annotations) {
        validateAnnotationsAreValid(annotations);
        List<String> ignoredAnnotationDescriptors = new ArrayList<>();
        for (Class<?> ignoredAnnotation : annotations) {
            ignoredAnnotationDescriptors.add(Type.getDescriptor(ignoredAnnotation));
        }
        config = config.withIgnoredAnnotations(ignoredAnnotationDescriptors);
        return this;
    }

    /**
     * Signals that T is part of an inheritance hierarchy where {@code equals}
     * is overridden. Call this method if T has overridden {@code equals} and
     * {@code hashCode}, and one or more of T's superclasses have as well.
     * <p>
     * T itself does not necessarily have to have subclasses that redefine
     * {@code equals} and {@code hashCode}.
     *
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withRedefinedSuperclass() {
        config = config.withRedefinedSuperclass();
        return this;
    }

    /**
     * Supplies a reference to a subclass of T in which {@code equals} is
     * overridden. Calling this method is mandatory if {@code equals} is not
     * final and a strong verification is performed.
     *
     * Note that, for each subclass that overrides {@code equals},
     * {@link EqualsVerifier} should be used as well to verify its
     * adherence to the contracts.
     *
     * @param redefinedSubclass A subclass of T for which no instance can be
     *          equal to any instance of T.
     * @return {@code this}, for easy method chaining.
     *
     * @see Warning#STRICT_INHERITANCE
     */
    public EqualsVerifier<T> withRedefinedSubclass(Class<? extends T> redefinedSubclass) {
        config = config.withRedefinedSubclass(redefinedSubclass);
        return this;
    }

    /**
     * Signals that T caches its hashCode, instead of re-calculating it each
     * time the {@code hashCode()} method is called.
     *
     * There are 3 conditions to verify cached hashCodes:
     *
     * First, the class under test must have a private int field that contains
     * the cached hashCode.
     *
     * Second, the class under test must have a private method that calculates
     * the hashCode. The method must return an int and may not take any
     * parameters. It should be used by the constructor or the hashCode method
     * of the class under test to initialize the cached hashCode. This may lead
     * to slightly awkward production code, but unfortunately, it is necessary
     * for EqualsVerifier to verify that the hashCode is correct.
     *
     * Finally, only immutable objects can be verified. In other words,
     * {@code withCachedHashCode} can not be used when
     * {@link Warning#NONFINAL_FIELDS} is suppressed.
     *
     * @param cachedHashCodeField
     *          The name of the field which stores the cached hash code.
     * @param calculateHashCodeMethod
     *          The name of the method which recomputes the hash code. It
     *          should return an int and take no parameters.
     * @param example
     *          An instance of the class under test, to verify that the
     *          hashCode has been initialized properly.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifier<T> withCachedHashCode(String cachedHashCodeField, String calculateHashCodeMethod, T example) {
        CachedHashCodeInitializer<T> cachedHashCodeInitializer =
                new CachedHashCodeInitializer<>(config.getType(), cachedHashCodeField, calculateHashCodeMethod, example);
        config = config.withCachedHashCodeInitializer(cachedHashCodeInitializer);
        return this;
    }

    private void assertNoNonnullFields() {
        if (!config.getNonnullFields().isEmpty() && config.getWarningsToSuppress().contains(Warning.NULL_FIELDS)) {
            throw new IllegalArgumentException("You can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both.");
        }
    }

    private void assertNoExistingExcludedFields() {
        assertNoExistingFields(config.getExcludedFields());
    }

    private void assertNoExistingIncludedFields() {
        assertNoExistingFields(config.getIncludedFields());
    }

    private void assertNoExistingFields(Set<String> fields) {
        if (!fields.isEmpty()) {
            throw new IllegalArgumentException("You can call either withOnlyTheseFields or withIgnoredFields, but not both.");
        }
    }

    private void validateFieldNamesExist(List<String> givenFields) {
        Set<String> actualFieldNames = config.getActualFields();
        for (String field : givenFields) {
            if (!actualFieldNames.contains(field)) {
                throw new IllegalArgumentException("Class " + config.getType().getSimpleName() + " does not contain field " + field + ".");
            }
        }
    }

    private void validateAnnotationsAreValid(Class<?>... givenAnnotations) {
        for (Class<?> annotation : givenAnnotations) {
            if (!annotation.isAnnotation()) {
                throw new IllegalArgumentException("Class " + annotation.getCanonicalName() + " is not an annotation.");
            }
        }
    }

    /**
     * Performs the verification of the contracts for {@code equals} and
     * {@code hashCode}.
     *
     * @throws AssertionError If the contract is not met, or if
     *          {@link EqualsVerifier}'s preconditions do not hold.
     */
    public void verify() {
        try {
            performVerification();
        }
        catch (MessagingException e) {
            handleError(e, e.getCause());
        }
        catch (Throwable e) {
            handleError(e, e);
        }
    }

    private void handleError(Throwable messageContainer, Throwable trueCause) {
        boolean showCauseExceptionInMessage = trueCause != null && trueCause.equals(messageContainer);
        Formatter message = Formatter.of(
                "%%%%\nFor more information, go to: http://www.jqno.nl/equalsverifier/errormessages",
                showCauseExceptionInMessage ? trueCause.getClass().getSimpleName() + ": " : "",
                messageContainer.getMessage() == null ? "" : messageContainer.getMessage());

        AssertionError error = new AssertionError(message.format());
        error.initCause(trueCause);
        throw error;
    }

    private void performVerification() {
        if (config.getType().isEnum()) {
            return;
        }

        verifyWithoutExamples();
        ensureUnequalExamples();
        verifyWithExamples();
    }

    private void verifyWithoutExamples() {
        Checker[] checkers = {
            new SignatureChecker<>(config),
            new AbstractDelegationChecker<>(config),
            new NullChecker<>(config),
            new CachedHashCodeChecker<>(config)
        };

        for (Checker checker : checkers) {
            checker.check();
        }
    }

    private void ensureUnequalExamples() {
        if (config.getUnequalExamples().size() > 0) {
            return;
        }

        TypeTag tag = config.getTypeTag();
        ClassAccessor<T> classAccessor = config.createClassAccessor();

        List<T> unequalExamples = new ArrayList<>();
        unequalExamples.add(classAccessor.getRedObject(tag));
        unequalExamples.add(classAccessor.getBlackObject(tag));
        config = config.withUnequalExamples(unequalExamples);
    }

    private void verifyWithExamples() {
        Checker[] checkers = {
            new ExamplesChecker<>(config),
            new HierarchyChecker<>(config),
            new FieldsChecker<>(config)
        };

        for (Checker checker : checkers) {
            checker.check();
        }
    }

    @SafeVarargs
    private static <T> List<T> buildListOfAtLeastOne(T first, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }

        List<T> result = new ArrayList<>();
        result.add(first);
        addArrayElementsToList(result, more);

        return result;
    }

    @SafeVarargs
    private static <T> List<T> buildListOfAtLeastTwo(T first, T second, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("Second example is null.");
        }

        List<T> result = new ArrayList<>();
        result.add(first);
        result.add(second);
        addArrayElementsToList(result, more);

        return result;
    }

    @SafeVarargs
    private static <T> void addArrayElementsToList(List<T> list, T... more) {
        if (more != null) {
            for (T e : more) {
                if (e == null) {
                    throw new IllegalArgumentException("One of the examples is null.");
                }
                list.add(e);
            }
        }
    }

    private static <T> boolean listContainsDuplicates(List<T> list) {
        return list.size() != new HashSet<>(list).size();
    }

    /**
     * Helper class for
     * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
     * Its purpose is to make sure, at compile time, that a list of unequal
     * examples is given, as well as the list of equal examples that are
     * supplied to the aforementioned method.
     */
    public static final class RelaxedEqualsVerifierHelper<T> {
        private final Class<T> type;
        private final List<T> equalExamples;

        /**
         * Private constructor, only to be called by
         * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
         */
        private RelaxedEqualsVerifierHelper(Class<T> type, List<T> examples) {
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
        public EqualsVerifier<T> andUnequalExample(T example) {
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
        public final EqualsVerifier<T> andUnequalExamples(T first, T... more) {
            List<T> unequalExamples = buildListOfAtLeastOne(first, more);
            if (listContainsDuplicates(unequalExamples)) {
                throw new IllegalArgumentException("Two objects are equal to each other.");
            }
            for (T example : unequalExamples) {
                if (equalExamples.contains(example)) {
                    throw new IllegalArgumentException("An equal example also appears as unequal example.");
                }
            }

            Configuration<T> config = Configuration.of(type)
                    .withEqualExamples(equalExamples)
                    .withUnequalExamples(unequalExamples);
            return new EqualsVerifier<>(config)
                    .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED);
        }
    }
}
