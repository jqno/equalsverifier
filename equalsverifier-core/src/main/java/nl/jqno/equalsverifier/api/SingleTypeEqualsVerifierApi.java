package nl.jqno.equalsverifier.api;

import java.util.*;
import java.util.function.Function;

import nl.jqno.equalsverifier.*;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.checkers.*;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.PrefabValuesApi;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.util.*;
import nl.jqno.equalsverifier.internal.util.Formatter;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Helps to construct an {@link EqualsVerifier} test with a fluent API.
 *
 * @param <T> The class under test.
 */
public class SingleTypeEqualsVerifierApi<T> implements EqualsVerifierApi<T> {

    private final Class<T> type;
    private final Set<String> actualFields;

    private EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private boolean usingGetClass = false;
    private boolean hasRedefinedSuperclass = false;
    private Class<? extends T> redefinedSubclass = null;
    private FactoryCache factoryCache = new FactoryCache();
    private FieldCache fieldCache = new FieldCache();
    private CachedHashCodeInitializer<T> cachedHashCodeInitializer = CachedHashCodeInitializer.passthrough();
    private Function<String, String> fieldnameToGetter = null;
    private Set<String> allExcludedFields = new HashSet<>();
    private Set<String> allIncludedFields = new HashSet<>();
    private Set<String> nonnullFields = new HashSet<>();
    private Set<String> ignoredAnnotationClassNames = new HashSet<>();
    private List<T> equalExamples = new ArrayList<>();
    private List<T> unequalExamples = new ArrayList<>();
    private final Objenesis objenesis;

    /**
     * Constructor.
     *
     * @param type The class for which the {@code equals} method should be tested.
     */
    public SingleTypeEqualsVerifierApi(Class<T> type) {
        this(type, new ObjenesisStd());
    }

    /**
     * Constructor.
     *
     * @param type      The class for which the {@code equals} method should be tested.
     * @param objenesis To instantiate non-record classes.
     */
    public SingleTypeEqualsVerifierApi(Class<T> type, Objenesis objenesis) {
        this.type = type;
        this.actualFields = FieldNameExtractor.extractFieldNames(type);
        this.objenesis = objenesis;
    }

    /**
     * Constructor.
     *
     * @param type               The class for which the {@code equals} method should be tested.
     * @param warningsToSuppress A list of warnings to suppress in {@code EqualsVerifier}.
     * @param factoryCache       Factories that can be used to create values.
     * @param objenesis          To instantiate non-record classes.
     * @param usingGetClass      Whether {@code getClass} is used in the implementation of the {@code
     *     equals}            method, instead of an {@code instanceof} check.
     * @param converter          A function that converts from field name to getter name.
     */
    public SingleTypeEqualsVerifierApi(
            Class<T> type,
            EnumSet<Warning> warningsToSuppress,
            FactoryCache factoryCache,
            Objenesis objenesis,
            boolean usingGetClass,
            Function<String, String> converter) {
        this(type, objenesis);
        this.warningsToSuppress = EnumSet.copyOf(warningsToSuppress);
        this.factoryCache = this.factoryCache.merge(factoryCache);
        this.usingGetClass = usingGetClass;
        this.fieldnameToGetter = converter;
    }

    /**
     * Constructor, only to be called by {@link RelaxedEqualsVerifierApi#andUnequalExamples(Object, Object[])}.
     */
    /* package protected */ SingleTypeEqualsVerifierApi(Class<T> type, List<T> equalExamples, List<T> unequalExamples) {
        this(type, new ObjenesisStd());
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        Validations.validateWarnings(warningsToSuppress);
        Validations.validateWarningsAndFields(warningsToSuppress, allIncludedFields, allExcludedFields);
        Validations.validateNonnullFields(nonnullFields, warningsToSuppress);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> SingleTypeEqualsVerifierApi<T> withPrefabValues(Class<S> otherType, S red, S blue) {
        PrefabValuesApi.addPrefabValues(factoryCache, objenesis, otherType, red, blue);
        return this;
    }

    /**
     * Adds prefabricated values for instance fields with a given name (and only the fields with the given name) that
     * EqualsVerifier cannot instantiate by itself.
     *
     * @param <S>       The class of the prefabricated values.
     * @param fieldName The name of the field that the prefabricated values are linked to.
     * @param red       An instance of {@code S}.
     * @param blue      Another instance of {@code S}, not equal to {@code red}.
     * @return {@code this}, for easy method chaining.
     * @throws NullPointerException     If {@code red} or {@code blue} is null, or if the named field does not exist in
     *                                      the class.
     * @throws IllegalArgumentException If {@code red} equals {@code blue}.
     */
    @CheckReturnValue
    public <S> SingleTypeEqualsVerifierApi<T> withPrefabValuesForField(String fieldName, S red, S blue) {
        Validations.validateFieldNamesExist(type, Arrays.asList(fieldName), actualFields);
        PrefabValuesApi.addPrefabValuesForField(fieldCache, objenesis, type, fieldName, red, blue);
        return withNonnullFields(fieldName);
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> SingleTypeEqualsVerifierApi<T> withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> SingleTypeEqualsVerifierApi<T> withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> usingGetClass() {
        this.usingGetClass = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withFieldnameToGetterConverter(Function<String, String> converter) {
        this.fieldnameToGetter = converter;
        return this;
    }

    /**
     * Signals that all given fields are not relevant for the {@code equals} contract. {@code
     * EqualsVerifier} will not fail if one of these fields does not affect the outcome of {@code
     * equals}, but it will fail if one of these fields does affect the outcome of {@code equals}.
     *
     * <p>
     * Note that these fields will still be used to test for null-ness, among other things.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withIgnoredFields(String... fields) {
        return withFieldsAddedAndValidated(allExcludedFields, Arrays.asList(fields));
    }

    /**
     * Signals that all given fields, and <em>only</em> the given fields, are relevant for the {@code equals} contract.
     * {@code EqualsVerifier} will fail if one of these fields does not affect the outcome of {@code equals}, and it
     * will fail if a field that isn't listed here, does affect the outcome of {@code equals}.
     *
     * @param fields Fields that should be ignored.
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withOnlyTheseFields(String... fields) {
        return withFieldsAddedAndValidated(allIncludedFields, Arrays.asList(fields));
    }

    private SingleTypeEqualsVerifierApi<T> withFieldsAddedAndValidated(
            Set<String> collection,
            List<String> specifiedFields) {
        collection.addAll(specifiedFields);

        Validations.validateFields(allIncludedFields, allExcludedFields);
        Validations.validateFieldNamesExist(type, specifiedFields, actualFields);
        Validations.validateWarningsAndFields(warningsToSuppress, allIncludedFields, allExcludedFields);
        return this;
    }

    /**
     * Signals that all given fields can never be null, and {@code EqualsVerifier} therefore doesn't have to verify that
     * proper null checks are in place for these fields.
     *
     * <p>
     * This can be used instead of {link #suppress(Warning...)}, which provides the same behaviour for all fields, when
     * only some fields are never null but others are.
     *
     * @param fields Fields that can never be null.
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withNonnullFields(String... fields) {
        List<String> fieldsAsList = Arrays.asList(fields);
        nonnullFields.addAll(fieldsAsList);
        Validations.validateFieldNamesExist(type, fieldsAsList, actualFields);
        Validations.validateNonnullFields(nonnullFields, warningsToSuppress);
        return this;
    }

    /**
     * Signals that all given annotations are to be ignored by EqualsVerifier.
     *
     * <p>
     * For instance, EqualsVerifier normally doesn't perform null verifications on fields marked with an
     * {@code @Nonnull} annotation. However, if this method is called with a {@code
     * Nonnull.class} parameter, the null verifications will be performed after all.
     *
     * @param annotations Annotations to ignore.
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withIgnoredAnnotations(Class<?>... annotations) {
        Validations.validateGivenAnnotations(annotations);
        for (Class<?> ignoredAnnotation : annotations) {
            ignoredAnnotationClassNames.add(ignoredAnnotation.getCanonicalName());
        }
        return this;
    }

    /**
     * Signals that T is part of an inheritance hierarchy where {@code equals} is overridden. Call this method if T has
     * overridden {@code equals} and {@code hashCode}, and one or more of T's superclasses have as well.
     *
     * <p>
     * T itself does not necessarily have to have subclasses that redefine {@code equals} and {@code hashCode}.
     *
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withRedefinedSuperclass() {
        this.hasRedefinedSuperclass = true;
        return this;
    }

    /**
     * Supplies a reference to a subclass of T in which {@code equals} is overridden. Calling this method is mandatory
     * if {@code equals} is not final and a strong verification is performed.
     *
     * <p>
     * Note that, for each subclass that overrides {@code equals}, {@link EqualsVerifier} should be used as well to
     * verify its adherence to the contracts.
     *
     * @param subclass A subclass of T for which no instance can be equal to any instance of T.
     * @return {@code this}, for easy method chaining.
     * @see Warning#STRICT_INHERITANCE
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withRedefinedSubclass(Class<? extends T> subclass) {
        this.redefinedSubclass = subclass;
        return this;
    }

    /**
     * Signals that T caches its hashCode, instead of re-calculating it each time the {@code
     * hashCode()} method is called.
     *
     * <p>
     * There are 3 conditions to verify cached hashCodes:
     *
     * <p>
     * First, the class under test must have a private int field that contains the cached hashCode.
     *
     * <p>
     * Second, the class under test must have a private method that calculates the hashCode. The method must return an
     * int and may not take any parameters. It should be used by the constructor or the hashCode method of the class
     * under test to initialize the cached hashCode. This may lead to slightly awkward production code, but
     * unfortunately, it is necessary for EqualsVerifier to verify that the hashCode is correct.
     *
     * <p>
     * Finally, only immutable objects can be verified. In other words, {@code
     * withCachedHashCode} can not be used when {@link Warning#NONFINAL_FIELDS} is suppressed.
     *
     * @param cachedHashCodeField     The name of the field which stores the cached hash code.
     * @param calculateHashCodeMethod The name of the method which recomputes the hash code. It should return an int and
     *                                    take no parameters.
     * @param example                 An instance of the class under test, to verify that the hashCode has been
     *                                    initialized properly.
     * @return {@code this}, for easy method chaining.
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withCachedHashCode(
            String cachedHashCodeField,
            String calculateHashCodeMethod,
            T example) {
        cachedHashCodeInitializer =
                new CachedHashCodeInitializer<>(type, cachedHashCodeField, calculateHashCodeMethod, example);
        return this;
    }

    /**
     * Signals that T uses Lombok to cache its hashCode, instead of re-calculating it each time the {@code hashCode()}
     * method is called.
     *
     * @param example An instance of the class under test, to verify that the hashCode has been initialized properly.
     * @return {@code this}, for easy method chaining.
     * @see #withCachedHashCode(String, String, Object)
     */
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withLombokCachedHashCode(T example) {
        cachedHashCodeInitializer = CachedHashCodeInitializer.lombokCachedHashcode(example);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated No longer needed; this happens automatically.
     */
    @Deprecated
    @Override
    @CheckReturnValue
    public SingleTypeEqualsVerifierApi<T> withResetCaches() {
        return this;
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and throws an
     * {@link AssertionError} if there is a problem.
     *
     * @throws AssertionError If the contract is not met, or if {@link EqualsVerifier}'s preconditions do not hold.
     */
    public void verify() {
        try {
            performVerification();
        }
        catch (MessagingException e) {
            throw new AssertionError(buildErrorMessage(e.getDescription(), true), e);
        }
        catch (Throwable e) {
            throw new AssertionError(buildErrorMessage(e.getMessage(), true), e);
        }
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and returns an
     * {@link EqualsVerifierReport} with the results of the verification.
     *
     * @return An {@link EqualsVerifierReport} that indicates whether the contract is met and whether
     *             {@link EqualsVerifier}'s preconditions hold.
     */
    public EqualsVerifierReport report() {
        return report(true);
    }

    /**
     * Performs the verification of the contracts for {@code equals} and {@code hashCode} and returns an
     * {@link EqualsVerifierReport} with the results of the verification.
     *
     * @param showUrl Whether or not to show the url at the end of the error message.
     * @return An {@link EqualsVerifierReport} that indicates whether the contract is met and whether
     *             {@link EqualsVerifier}'s preconditions hold.
     */
    public EqualsVerifierReport report(boolean showUrl) {
        try {
            performVerification();
            return EqualsVerifierReport.success(type);
        }
        catch (MessagingException e) {
            return EqualsVerifierReport.failure(type, buildErrorMessage(e.getDescription(), showUrl), e);
        }
        catch (Throwable e) {
            return EqualsVerifierReport.failure(type, buildErrorMessage(e.getMessage(), showUrl), e);
        }
    }

    private String buildErrorMessage(String description, boolean showUrl) {
        String message = description == null ? "<no message>" : description;
        String result =
                Formatter.of("EqualsVerifier found a problem in class %%.\n-> %%", type.getName(), message).format();
        if (showUrl) {
            result += "\n\n" + ErrorMessage.suffix();
        }
        return result;
    }

    private void performVerification() {
        if (type.isEnum() || type.isInterface()) {
            return;
        }
        Validations.validateClassCanBeVerified(type);

        Configuration<T> config = buildConfig();
        Context<T> context = new Context<>(config, factoryCache, fieldCache, objenesis);
        Validations
                .validateProcessedAnnotations(
                    type,
                    config.getAnnotationCache(),
                    warningsToSuppress,
                    allIncludedFields,
                    allExcludedFields);

        verifyWithoutExamples(context);
        verifyWithExamples(context);
    }

    private Configuration<T> buildConfig() {
        return Configuration
                .build(
                    type,
                    allExcludedFields,
                    allIncludedFields,
                    nonnullFields,
                    fieldCache.getFieldNames(),
                    cachedHashCodeInitializer,
                    hasRedefinedSuperclass,
                    redefinedSubclass,
                    usingGetClass,
                    warningsToSuppress,
                    fieldnameToGetter,
                    ignoredAnnotationClassNames,
                    actualFields,
                    equalExamples,
                    unequalExamples);
    }

    private void verifyWithoutExamples(Context<T> context) {
        Configuration<T> config = context.getConfiguration();
        Checker[] checkers = { new SignatureChecker<>(context), new AbstractDelegationChecker<>(context),
                new NullChecker<>(context), new RecordChecker<>(context), new CachedHashCodeChecker<>(config) };

        for (Checker checker : checkers) {
            checker.check();
        }
    }

    private void verifyWithExamples(Context<T> context) {
        Checker[] checkers = { new ExamplesChecker<>(context), new HierarchyChecker<>(context),
                new FieldsChecker<>(context), new MapEntryHashCodeRequirementChecker<>(context) };

        for (Checker checker : checkers) {
            checker.check();
        }
    }
}
