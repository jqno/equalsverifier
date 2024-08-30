package nl.jqno.equalsverifier.internal.checkers;

import java.util.function.Predicate;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.*;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;

public class FieldsChecker<T> implements Checker {

    private final Configuration<T> config;
    private final ArrayFieldCheck<T> arrayFieldCheck;
    private final FloatAndDoubleFieldCheck<T> floatAndDoubleFieldCheck;
    private final MutableStateFieldCheck<T> mutableStateFieldCheck;
    private final ReflexivityFieldCheck<T> reflexivityFieldCheck;
    private final SignificantFieldCheck<T> significantFieldCheck;
    private final SymmetryFieldCheck<T> symmetryFieldCheck;
    private final TransientFieldsCheck<T> transientFieldsCheck;
    private final TransitivityFieldCheck<T> transitivityFieldCheck;
    private final StringFieldCheck<T> stringFieldCheck;
    private final BigDecimalFieldCheck<T> bigDecimalFieldCheck;
    private final JpaLazyGetterFieldCheck<T> jpaLazyGetterFieldCheck;

    public FieldsChecker(Configuration<T> config) {
        this.config = config;

        final TypeTag typeTag = config.getTypeTag();
        final SubjectCreator<T> subjectCreator = config.getSubjectCreator();

        final String cachedHashCodeFieldName = config
            .getCachedHashCodeInitializer()
            .getCachedHashCodeFieldName();
        final Predicate<FieldAccessor> isCachedHashCodeField = a ->
            a.getFieldName().equals(cachedHashCodeFieldName);

        this.arrayFieldCheck =
            new ArrayFieldCheck<>(subjectCreator, config.getCachedHashCodeInitializer());
        this.floatAndDoubleFieldCheck = new FloatAndDoubleFieldCheck<>(subjectCreator);
        this.mutableStateFieldCheck =
            new MutableStateFieldCheck<>(subjectCreator, isCachedHashCodeField);
        this.reflexivityFieldCheck = new ReflexivityFieldCheck<>(config);
        this.significantFieldCheck = new SignificantFieldCheck<>(config, isCachedHashCodeField);
        this.symmetryFieldCheck = new SymmetryFieldCheck<>(subjectCreator);
        this.transientFieldsCheck =
            new TransientFieldsCheck<>(subjectCreator, typeTag, config.getAnnotationCache());
        this.transitivityFieldCheck = new TransitivityFieldCheck<>(subjectCreator);
        this.stringFieldCheck =
            new StringFieldCheck<>(
                subjectCreator,
                config.getPrefabValues(),
                config.getCachedHashCodeInitializer()
            );
        this.bigDecimalFieldCheck =
            new BigDecimalFieldCheck<>(subjectCreator, config.getCachedHashCodeInitializer());
        this.jpaLazyGetterFieldCheck = new JpaLazyGetterFieldCheck<>(config);
    }

    @Override
    public void check() {
        ClassAccessor<T> classAccessor = config.getClassAccessor();
        FieldInspector<T> inspector = new FieldInspector<>(classAccessor.getType(), config);

        if (!classAccessor.isEqualsInheritedFromObject()) {
            inspector.check(arrayFieldCheck);
            inspector.check(floatAndDoubleFieldCheck);
            inspector.check(reflexivityFieldCheck);
        }

        if (!ignoreMutability(config.getType())) {
            inspector.check(mutableStateFieldCheck);
        }

        if (!config.getWarningsToSuppress().contains(Warning.TRANSIENT_FIELDS)) {
            inspector.check(transientFieldsCheck);
        }

        inspector.check(significantFieldCheck);
        inspector.check(symmetryFieldCheck);
        inspector.check(transitivityFieldCheck);
        inspector.check(stringFieldCheck);

        if (!config.getWarningsToSuppress().contains(Warning.BIGDECIMAL_EQUALITY)) {
            inspector.check(bigDecimalFieldCheck);
        }

        AnnotationCache cache = config.getAnnotationCache();
        if (
            cache.hasClassAnnotation(config.getType(), SupportedAnnotations.ENTITY) &&
            !config.getWarningsToSuppress().contains(Warning.JPA_GETTER)
        ) {
            inspector.check(jpaLazyGetterFieldCheck);
        }
    }

    private boolean ignoreMutability(Class<?> type) {
        AnnotationCache cache = config.getAnnotationCache();
        return (
            config.getWarningsToSuppress().contains(Warning.NONFINAL_FIELDS) ||
            cache.hasClassAnnotation(type, SupportedAnnotations.IMMUTABLE) ||
            cache.hasClassAnnotation(type, SupportedAnnotations.ENTITY)
        );
    }
}
