package nl.jqno.equalsverifier.internal.valueproviders;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.exceptions.InstantiatorException;
import nl.jqno.equalsverifier.internal.instantiators.Instantiator;
import nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.util.Rethrow;
import org.objenesis.Objenesis;

/**
 * Creates a subject, i.e. an instance of the class that is currently being tested by EqualsVerifier.
 */
public class SubjectCreator<T> {

    private final TypeTag typeTag;
    private final Class<T> type;
    private final Class<T> actualType;
    private final Configuration<T> config;
    private final ValueProvider valueProvider;
    private final Objenesis objenesis;
    private final Instantiator<? extends T> instantiator;
    private final boolean forceFinalMeansFinal;

    /**
     * Constructor.
     *
     * @param config               A configuration object.
     * @param valueProvider        To provide values for the fields of the subject.
     * @param objenesis            Needed by InstanceCreator to instantiate non-record classes.
     * @param forceFinalMeansFinal Force "final means final" (JEP 500) mode.
     */
    public SubjectCreator(
            Configuration<T> config,
            ValueProvider valueProvider,
            Objenesis objenesis,
            boolean forceFinalMeansFinal) {
        this.typeTag = config.typeTag();
        this.type = typeTag.getType();
        this.config = config;
        this.valueProvider = valueProvider;
        this.objenesis = objenesis;
        this.forceFinalMeansFinal = forceFinalMeansFinal;
        this.actualType =
                SubtypeManager.findInstantiableSubclass(ClassProbe.of(type), valueProvider, Attributes.empty());
        this.instantiator =
                InstantiatorFactory.of(ClassProbe.of(actualType), config.factory(), objenesis, forceFinalMeansFinal);
    }

    /**
     * Creates an instance with all values set with prefab values assigned to their respective field names or types, or,
     * if no prefab values are given, values created by the {@link ValueProvider}.
     *
     * @return A plain instance.
     */
    public T plain() {
        return createInstance(empty());
    }

    /**
     * Creates a {@link #plain()} instance, but with the given field set to its type's default value: null for object
     * references, 0 for numbers, false for booleans.
     *
     * @param field The field to default.
     * @return A plain instance with a field defaulted.
     */
    public T withFieldDefaulted(Field field) {
        return createInstance(with(field, null));
    }

    /**
     * Creates an instance with all values set to their type's default value: null for object references, 0 for numbers,
     * false for booleans.
     *
     * @return An instance with all fields defaulted.
     */
    public T withAllFieldsDefaulted() {
        Map<Field, Object> values = empty();
        for (FieldProbe p : fields()) {
            values.put(p.getField(), null);
        }
        return createInstance(values);
    }

    /**
     * Creates an instance with all fields matching the predicate set to their type's default value: null for object
     * references, 0 for numbers, false for booleans, except for fields that don't match the predicate, which will get a
     * {@link #plain()} value.
     *
     * @param predicate A predicate to determine if the field should be defaulted.
     * @return An instance with all matching fields defaulted and all non-matching fields set to their {@link #plain()}
     *             value.
     */
    public T withAllMatchingFieldsDefaulted(Predicate<Field> predicate) {
        Map<Field, Object> values = empty();
        for (FieldProbe p : fields()) {
            if (predicate.test(p.getField())) {
                values.put(p.getField(), null);
            }
        }
        return createInstance(values);
    }

    /**
     * Creates an instance with all values set to their type's default value: null for object references, 0 for numbers,
     * false for booleans; except for the given field which is set to its {@link #plain()} value.
     *
     * @param field The field that should not be defaulted.
     * @return An instance with all fields defaulted except for {@code field}.
     */
    public T withAllFieldsDefaultedExcept(Field field) {
        Map<Field, Object> values = empty();
        for (FieldProbe p : fields()) {
            Field f = p.getField();
            if (!f.equals(field)) {
                values.put(f, null);
            }
        }
        return createInstance(values);
    }

    /**
     * Creates a {@link #plain()} instance, but with the given field set to the given value.
     *
     * @param field The field to assign the given value.
     * @param value The value to assign to the given field.
     * @return A plain instance with one field assigned the given value.
     */
    public T withFieldSetTo(Field field, Object value) {
        return createInstance(with(field, value));
    }

    /**
     * Creates a {@link #plain()} instance, but with the given field set to another value.
     *
     * @param field The field to change.
     * @return A plain instance with a field changed.
     */
    public T withFieldChanged(Field field) {
        if (FieldProbe.of(field).isStatic()) {
            return plain();
        }
        Object value = valuesFor(field).blue();
        return createInstance(with(field, value));
    }

    /**
     * Creates a {@link #plain()} instance, but with all fields set to another value.
     *
     * @return A plain instance with all fields changed.
     */
    public T withAllFieldsChanged() {
        Map<Field, Object> values = empty();
        for (FieldProbe p : fields()) {
            Field f = p.getField();
            Object value = valuesFor(f).blue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    /**
     * Creates a {@link #plain()} instance, but with all fields that are declared in the current class set to another
     * value. Fields coming from the superclasses get their {@link #plain()} value.
     *
     * @return A plain instance with all non-inherited fields changed.
     */
    public T withAllFieldsShallowlyChanged() {
        Map<Field, Object> values = empty();
        for (FieldProbe p : nonSuperFields()) {
            Field f = p.getField();
            Object value = valuesFor(f).blue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    /**
     * Creates a new instance of the superclass of the current class, with all fields that exist within that superclass
     * set to the same value as their counterparts from {@code original}.
     *
     * @param original The instance to copy.
     * @return An instance of the givenoriginal's superclass, but otherwise a copy of the original.
     */
    public Object copyIntoSuperclass(T original) {
        var actualSuperType = SubtypeManager
                .findInstantiableSubclass(ClassProbe.of(type.getSuperclass()), valueProvider, Attributes.empty());
        Instantiator<? super T> superCreator =
                InstantiatorFactory.of(ClassProbe.of(actualSuperType), objenesis, forceFinalMeansFinal);
        return superCreator.copy(original);
    }

    /**
     * Creates a new instance of the given subclass of the current class, with all fields that also exist in the current
     * class set to the same value as their counterparts from {@code original}. All fields declared in the subclass are
     * set to their {@link #plain()} values.
     *
     * @param <S>             A subtype of original's type.
     * @param original        The instance to copy.
     * @param subType         A subtype of original's type.
     * @param subclassFactory A factory to instantiate the subtype.
     * @return An instance of the given subType, but otherwise a copy of the given original.
     */
    public <S extends T> S copyIntoSubclass(T original, Class<S> subType, InstanceFactory<S> subclassFactory) {
        var actualSubType =
                SubtypeManager.findInstantiableSubclass(ClassProbe.of(subType), valueProvider, Attributes.empty());
        try {
            Instantiator<S> subCreator = InstantiatorFactory
                    .of(ClassProbe.of(actualSubType), subclassFactory, objenesis, forceFinalMeansFinal);
            return subCreator.copy(original);
        }
        catch (InstantiatorException e) {
            var msg = """
                      Cannot instantiate a subclass of %% (attempted subclass: %%).
                      Use an overload of #withFactory() to specify a subclass.""";
            throw new InstantiatorException(
                    Formatter.of(msg, type.getSimpleName(), actualSubType.getSimpleName()).format());
        }
    }

    private T createInstance(Map<Field, Object> givens) {
        Map<Field, Object> values = determineValues(givens);
        return Rethrow.rethrow(() -> instantiator.instantiate(values));
    }

    private Map<Field, Object> determineValues(Map<Field, Object> givens) {
        var values = new HashMap<Field, Object>(givens);
        for (FieldProbe p : fields()) {
            Field f = p.getField();
            boolean fieldIsAbsent = !values.containsKey(f);
            boolean fieldCannotBeNull = values.get(f) == null && !p.canBeDefault(config);
            if (fieldIsAbsent || fieldCannotBeNull) {
                Object value = valuesFor(f).red();
                values.put(f, value);
            }
        }
        return values;
    }

    private Map<Field, Object> empty() {
        return new HashMap<>();
    }

    private Map<Field, Object> with(Field f, Object v) {
        Map<Field, Object> result = empty();
        result.put(f, v);
        return result;
    }

    private FieldIterable fields() {
        return FieldIterable.ofIgnoringStatic(actualType);
    }

    private FieldIterable nonSuperFields() {
        // This should probably use `actualType` instead of `type`, but then we'd need to find
        // a way to include all fields from `type` and `actualType` together but without the fields
        // from `type`'s superclass. That's hard, and it doesn't seem to come up in practice. I'm
        // leaving this comment here as an explanation, in case it does come up at some point.
        return FieldIterable.ofIgnoringSuperAndStatic(type);
    }

    private Tuple<?> valuesFor(Field f) {
        return InstantiationUtil.valuesFor(f, typeTag, valueProvider, Attributes.named(f.getName()));
    }
}
