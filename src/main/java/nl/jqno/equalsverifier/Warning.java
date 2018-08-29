package nl.jqno.equalsverifier;

/**
 * Enum of warnings that can be suppressed in {@link EqualsVerifier}.
 *
 * @see EqualsVerifierApi#suppress(Warning...)
 */
public enum Warning {
    /**
     * Signals that not all fields are relevant in the {@code equals} contract.
     * {@code EqualsVerifier} will not fail if one or more fields do not affect
     * the outcome of {@code equals}.
     * <p>
     * Only applies to non-transient fields.
     */
    ALL_FIELDS_SHOULD_BE_USED,

    /**
     * Signals that non-final fields are not relevant in the {@code equals}
     * contract. {@code EqualsVerifier} will not fail if one or more non-final
     * fields do not affect the outcome of {@code equals}.
     * <p>
     * Only applies to non-transient fields.
     */
    ALL_NONFINAL_FIELDS_SHOULD_BE_USED,

    /**
     * Disables the check for reference equality on fields.
     * <p>
     * EqualsVerifier will check if the {@code equals} method calls equals on
     * the object fields of the class under test, instead of the {@code ==}
     * operator, since normally this signifies a mistake (e.g. comparing string
     * fields with {@code ==}).
     * <p>
     * However, sometimes {@code ==} is used intentionally, or the field in
     * question doesn't implement {@code equals} itself, so a call to the
     * {@code equals} method of that field is essentially a reference equality
     * check instead of a value equality check. In these cases, this warning can
     * be suppressed.
     */
    REFERENCE_EQUALITY,

    /**
     * Disables the check, when the {@code equals} method is overridden in the
     * class under test, that an instance of this class should be equal to an
     * identical copy of itself.
     * <p>
     * Normally, it is important that an object be equal to an identical copy of
     * itself: after all, this is the point of overriding {@code equals} in the
     * first place.
     * <p>
     * However, when the class is part of a hierarchy, but should be
     * (pseudo-)singleton, it can be useful to suppress this warning. This can
     * happen in certain implementations of the Null Object Pattern, for
     * example.
     * <p>
     * If this warning is suppressed, and it turns out that an instance of the
     * class under test is equal to an identical copy of itself after all,
     * {@link EqualsVerifier} will fail.
     */
    IDENTICAL_COPY,

    /**
     * Disables the check, when the {@code equals} method is overridden in the
     * class under test, that an instance of this class should be equal to an
     * identical copy of itself.
     * <p>
     * Normally, it is important that an object be equal to an identical copy of
     * itself: after all, this is the point of overriding {@code equals} in the
     * first place.
     * <p>
     * However, when the class is a kind of versioned entity and there is an
     * {@code id} field that is zero when the object is new, it is often the
     * case that two new objects are never equal to each other. In these cases,
     * it can be useful to suppress this warning.
     * <p>
     * You cannot use {@link #IDENTICAL_COPY} in these cases, because when the
     * {@code id}s are equal, the objects should be, too, and
     * {@link EqualsVerifier} would fail in this case.
     * <p>
     * If this warning is suppressed, and it turns out that an instance of the
     * class under test is equal to an identical copy of itself after all,
     * {@link EqualsVerifier} will NOT fail.
     */
    IDENTICAL_COPY_FOR_VERSIONED_ENTITY,

    /**
     * Disables the check that verifies {@code equals} is actually overridden.
     * <p>
     * Can be used when a whole package of classes is automatically scanned and
     * presented to EqualsVerifier, and one or more of them don't need to
     * override {@code equals}.
     */
    INHERITED_DIRECTLY_FROM_OBJECT,

    /**
     * Disables the example check for cached {@code hashCode}.
     * <p>
     * The example check verifies that the cached {@code hashCode} is properly
     * initialized. You can use this, if creating an example object is too
     * cumbersome. In this case, null can be passed as an example.
     * <p>
     * Note that suppressing this warning can be dangerous and should only be
     * done in unusual circumstances.
     */
    NO_EXAMPLE_FOR_CACHED_HASHCODE,

    /**
     * Disables checks for non-final fields on which {@code equals} and
     * {@code hashCode} depend.
     * <p>
     * {@link EqualsVerifier}'s standard behaviour is to disallow non-final
     * fields being used in {@code equals} and {@code hashCode} methods, since
     * classes that depend on non-final fields in these methods cannot reliably
     * be used in collections.
     * <p>
     * However, sometimes an external library requires that fields be non-final.
     * An example of this are Java Beans. In such a case, suppress this warning
     * to prevent {@link EqualsVerifier} from checking for non-final fields.
     */
    NONFINAL_FIELDS,

    /**
     * Disables checks for {@link NullPointerException} within {@code equals},
     * {@code hashCode} and {@code toString} methods.
     * <p>
     * Sometimes the constructor of a class makes sure no field can be null. If
     * this is the case, and if the fields cannot be made null later in the
     * lifecycle of the class by setters or other methods, suppress this warning
     * to disable the check for {@link NullPointerException}.
     */
    NULL_FIELDS,

    /**
     * Disables the check that all fields used in {@code equals} must also be
     * used in {@code hashCode}.
     * <p>
     * This is useful when bringing legacy systems under test, where you don't
     * want to change the existing {@code hashCode} behaviour but you do want
     * to use EqualsVerifier.
     * <p>
     * Note that {@code hashCode}s with higher distributions give better
     * performance when used in collections such as {@link java.util.HashMap}.
     * Therefore, if possible, you should use all fields that are used in
     * {@code equals}, in {@code hashCode} as well.
     */
    STRICT_HASHCODE,

    /**
     * Disables some of the stricter inheritance tests; use at your own risk!
     * <p>
     * {@link EqualsVerifier}'s standard behaviour, if T is not final and
     * neither are its {@code equals} and {@code hashCode} methods, is to
     * require a reference to a subclass of T for which no instance can be equal
     * to any instance of T, to make sure that subclasses that can redefine
     * {@code equals} or {@code hashCode} don't break the contract.
     * <p>
     * Some may find that too strict for their liking; suppressing this warning
     * disables that test.
     *
     * @see EqualsVerifier#withRedefinedSubclass(Class)
     */
    STRICT_INHERITANCE,

    /**
     * Disables the check that transient fields not be part of the
     * {@code equals} contract.
     * <p>
     * {@link EqualsVerifier}'s standard behaviour is to disallow transient
     * fields being used in {@code equals} and {@code hashCode} methods, since
     * these fields may not be restored to their original state after
     * deserialization, which would break {@code equals}.
     * <p>
     * If measures are taken that this will never happen, this warning can be
     * suppressed to disable {@link EqualsVerifier}'s transience test.
     */
    TRANSIENT_FIELDS
}
