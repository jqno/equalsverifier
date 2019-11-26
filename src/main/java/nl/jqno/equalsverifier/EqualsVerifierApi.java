package nl.jqno.equalsverifier;

import java.util.*;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.checkers.*;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.util.*;
import nl.jqno.equalsverifier.internal.util.Formatter;

/**
 * Helps to construct an {@link EqualsVerifier} test with a fluent API.
 *
 * @param <T> The class under test.
 */
public class EqualsVerifierApi<T> {
    private final Class<T> type;
    private final Set<String> actualFields;

    private EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private boolean usingGetClass = false;
    private boolean hasRedefinedSuperclass = false;
    private Class<? extends T> redefinedSubclass = null;
    private FactoryCache factoryCache = new FactoryCache();
    private CachedHashCodeInitializer<T> cachedHashCodeInitializer =
            CachedHashCodeInitializer.passthrough();
    private Set<String> allExcludedFields = new HashSet<>();
    private Set<String> allIncludedFields = new HashSet<>();
    private Set<String> nonnullFields = new HashSet<>();
    private Set<String> ignoredAnnotationClassNames = new HashSet<>();
    private List<T> equalExamples = new ArrayList<>();
    private List<T> unequalExamples = new ArrayList<>();

    /** Constructor, only to be called by {@link EqualsVerifier#forClass(Class)}. */
    /* package protected */ EqualsVerifierApi(Class<T> type) {
        this.type = type;
        actualFields = FieldNameExtractor.extractFieldNames(type);
    }

    /** Constructor, only to be called by {@link ConfiguredEqualsVerifier#forClass(Class)}. */
    /* package protected */ EqualsVerifierApi(
            Class<T> type,
            EnumSet<Warning> warningsToSuppress,
            FactoryCache factoryCache,
            boolean usingGetClass) {
        this(type);
        this.warningsToSuppress = warningsToSuppress;
        this.factoryCache = this.factoryCache.merge(factoryCache);
        this.usingGetClass = usingGetClass;
    }

    /**
     * Constructor, only to be called by {@link RelaxedEqualsVerifierApi#andUnequalExamples(Object,
     * Object[])}.
     */
    /* package protected */ EqualsVerifierApi(
            Class<T> type, List<T> equalExamples, List<T> unequalExamples) {
        this(type);
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
    }

    /**
     * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning} to see what warnings
     * can be suppressed.
     *
     * @param warnings A list of warnings to suppress in {@code EqualsVerifier}.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        Validations.validateWarnings(warningsToSuppress);
        Validations.validateWarningsAndFields(
                warningsToSuppress, allIncludedFields, allExcludedFields);
        Validations.validateNonnullFields(nonnullFields, warningsToSuppress);
        return this;
    }

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
    public <S> EqualsVerifierApi<T> withPrefabValues(Class<S> otherType, S red, S black) {
        PrefabValuesApi.addPrefabValues(factoryCache, otherType, red, black);
        return this;
    }

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
            Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

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
            Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /**
     * Signals that {@code getClass} is used in the implementation of the {@code equals} method,
     * instead of an {@code instanceof} check.
     *
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> usingGetClass() {
        this.usingGetClass = true;
        return this;
    }

    /**
     * Signals that all given fields are not relevant for the {@code equals} contract. {@code
     * EqualsVerifier} will not fail if one of these fields does not affect the outcome of {@code
     * equals}, but it will fail if one of these fields does affect the outcome of {@code equals}.
     *
     * <p>Note that these fields will still be used to test for null-ness, among other things.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withIgnoredFields(String... fields) {
        return withFieldsAddedAndValidated(allExcludedFields, Arrays.asList(fields));
    }

    /**
     * Signals that all given fields, and <em>only</em> the given fields, are relevant for the
     * {@code equals} contract. {@code EqualsVerifier} will fail if one of these fields does not
     * affect the outcome of {@code equals}, and it will fail if a field that isn't listed here,
     * does affect the outcome of {@code equals}.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withOnlyTheseFields(String... fields) {
        return withFieldsAddedAndValidated(allIncludedFields, Arrays.asList(fields));
    }

    private EqualsVerifierApi<T> withFieldsAddedAndValidated(
            Set<String> collection, List<String> specifiedFields) {
        collection.addAll(specifiedFields);

        Validations.validateFields(allIncludedFields, allExcludedFields);
        Validations.validateFieldNamesExist(type, specifiedFields, actualFields);
        Validations.validateWarningsAndFields(
                warningsToSuppress, allIncludedFields, allExcludedFields);
        return this;
    }

    /**
     * Signals that all given fields can never be null, and {@code EqualsVerifier} therefore doesn't
     * have to verify that proper null checks are in place for these fields.
     *
     * <p>This can be used instead of {link #suppress(Warning...)}, which provides the same
     * behaviour for all fields, when only some fields are never null but others are.
     *
     * @param fields Fields that can never be null.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withNonnullFields(String... fields) {
        List<String> fieldsAsList = Arrays.asList(fields);
        nonnullFields.addAll(fieldsAsList);
        Validations.validateFieldNamesExist(type, fieldsAsList, actualFields);
        Validations.validateNonnullFields(nonnullFields, warningsToSuppress);
        return this;
    }

    /**
     * Signals that all given annotations are to be ignored by EqualsVerifier.
     *
     * <p>For instance, EqualsVerifier normally doesn't perform null verifications on fields marked
     * with an {@code @Nonnull} annotation. However, if this method is called with a {@code
     * Nonnull.class} parameter, the null verifications will be performed after all.
     *
     * @param annotations Annotations to ignore.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withIgnoredAnnotations(Class<?>... annotations) {
        Validations.validateGivenAnnotations(annotations);
        for (Class<?> ignoredAnnotation : annotations) {
            ignoredAnnotationClassNames.add(ignoredAnnotation.getCanonicalName());
        }
        return this;
    }

    /**
     * Signals that T is part of an inheritance hierarchy where {@code equals} is overridden. Call
     * this method if T has overridden {@code equals} and {@code hashCode}, and one or more of T's
     * superclasses have as well.
     *
     * <p>T itself does not necessarily have to have subclasses that redefine {@code equals} and
     * {@code hashCode}.
     *
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withRedefinedSuperclass() {
        this.hasRedefinedSuperclass = true;
        return this;
    }

    /**
     * Supplies a reference to a subclass of T in which {@code equals} is overridden. Calling this
     * method is mandatory if {@code equals} is not final and a strong verification is performed.
     *
     * <p>Note that, for each subclass that overrides {@code equals}, {@link EqualsVerifier} should
     * be used as well to verify its adherence to the contracts.
     *
     * @param subclass A subclass of T for which no instance can be equal to any instance of T.
     * @return {@code this}, for easy method chaining.
     * @see Warning#STRICT_INHERITANCE
     */
    public EqualsVerifierApi<T> withRedefinedSubclass(Class<? extends T> subclass) {
        this.redefinedSubclass = subclass;
        return this;
    }

    /**
     * Signals that T caches its hashCode, instead of re-calculating it each time the {@code
     * hashCode()} method is called.
     *
     * <p>There are 3 conditions to verify cached hashCodes:
     *
     * <p>First, the class under test must have a private int field that contains the cached
     * hashCode.
     *
     * <p>Second, the class under test must have a private method that calculates the hashCode. The
     * method must return an int and may not take any parameters. It should be used by the
     * constructor or the hashCode method of the class under test to initialize the cached hashCode.
     * This may lead to slightly awkward production code, but unfortunately, it is necessary for
     * EqualsVerifier to verify that the hashCode is correct.
     *
     * <p>Finally, only immutable objects can be verified. In other words, {@code
     * withCachedHashCode} can not be used when {@link Warning#NONFINAL_FIELDS} is suppressed.
     *
     * @param cachedHashCodeField The name of the field which stores the cached hash code.
     * @param calculateHashCodeMethod The name of the method which recomputes the hash code. It
     *     should return an int and take no parameters.
     * @param example An instance of the class under test, to verify that the hashCode has been
     *     initialized properly.
     * @return {@code this}, for easy method chaining.
     */
    public EqualsVerifierApi<T> withCachedHashCode(
            String cachedHashCodeField, String calculateHashCodeMethod, T example) {
        cachedHashCodeInitializer =
                new CachedHashCodeInitializer<>(
                        type, cachedHashCodeField, calculateHashCodeMethod, example);
        return this;
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and throws
     * an {@link AssertionError} if there is a problem.
     *
     * @throws AssertionError If the contract is not met, or if {@link EqualsVerifier}'s
     *     preconditions do not hold.
     */
    public void verify() {
        try {
            performVerification();
        } catch (MessagingException e) {
            throw new AssertionError(buildErrorMessage(e.getDescription()), e);
        } catch (Throwable e) {
            throw new AssertionError(buildErrorMessage(e.getMessage()), e);
        }
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and
     * returns an {@link EqualsVerifierReport} with the results of the verification.
     *
     * @return An {@link EqualsVerifierReport} that indicates whether the contract is met and
     *     whether {@link EqualsVerifier}'s preconditions hold.
     */
    public EqualsVerifierReport report() {
        try {
            performVerification();
            return EqualsVerifierReport.SUCCESS;
        } catch (MessagingException e) {
            return new EqualsVerifierReport(false, buildErrorMessage(e.getDescription()), e);
        } catch (Throwable e) {
            return new EqualsVerifierReport(false, buildErrorMessage(e.getMessage()), e);
        }
    }

    private String buildErrorMessage(String description) {
        return Formatter.of(
                        "EqualsVerifier found a problem in class %%.\n-> %%\n\nFor more information, go to: http://www.jqno.nl/equalsverifier/errormessages",
                        type.getSimpleName(), description)
                .format();
    }

    private void performVerification() {
        if (type.isEnum() || type.isInterface()) {
            return;
        }

        Configuration<T> config = buildConfig();
        Validations.validateProcessedAnnotations(
                type,
                config.getAnnotationCache(),
                warningsToSuppress,
                allIncludedFields,
                allExcludedFields);

        verifyWithoutExamples(config);
        verifyWithExamples(config);
    }

    private Configuration<T> buildConfig() {
        return Configuration.build(
                type,
                allExcludedFields,
                allIncludedFields,
                nonnullFields,
                cachedHashCodeInitializer,
                hasRedefinedSuperclass,
                redefinedSubclass,
                usingGetClass,
                warningsToSuppress,
                factoryCache,
                ignoredAnnotationClassNames,
                actualFields,
                equalExamples,
                unequalExamples);
    }

    private void verifyWithoutExamples(Configuration<T> config) {
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

    private void verifyWithExamples(Configuration<T> config) {
        Checker[] checkers = {
            new ExamplesChecker<>(config),
            new HierarchyChecker<>(config),
            new FieldsChecker<>(config)
        };

        for (Checker checker : checkers) {
            checker.check();
        }
    }
}
