package nl.jqno.equalsverifier.internal.checkers;

import java.util.function.Predicate;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.*;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Context;

public class FieldsChecker<T> implements Checker {

    private final Context<T> context;
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

    public FieldsChecker(Context<T> context) {
        this.context = context;
        this.config = context.getConfiguration();

        final TypeTag typeTag = config.typeTag();
        final SubjectCreator<T> subjectCreator = context.getSubjectCreator();

        final String cachedHashCodeFieldName = config.cachedHashCodeInitializer().getCachedHashCodeFieldName();
        final Predicate<FieldProbe> isCachedHashCodeField = p -> p.getName().equals(cachedHashCodeFieldName);

        this.arrayFieldCheck = new ArrayFieldCheck<>(subjectCreator, config.cachedHashCodeInitializer());
        this.floatAndDoubleFieldCheck = new FloatAndDoubleFieldCheck<>(subjectCreator);
        this.mutableStateFieldCheck = new MutableStateFieldCheck<>(subjectCreator, isCachedHashCodeField);
        this.reflexivityFieldCheck = new ReflexivityFieldCheck<>(context);
        this.significantFieldCheck = new SignificantFieldCheck<>(context, isCachedHashCodeField);
        this.symmetryFieldCheck = new SymmetryFieldCheck<>(subjectCreator);
        this.transientFieldsCheck = new TransientFieldsCheck<>(subjectCreator, typeTag, config.annotationCache());
        this.transitivityFieldCheck = new TransitivityFieldCheck<>(subjectCreator);
        this.stringFieldCheck =
                new StringFieldCheck<>(subjectCreator, context.getValueProvider(), config.cachedHashCodeInitializer());
        this.bigDecimalFieldCheck = new BigDecimalFieldCheck<>(subjectCreator, config.cachedHashCodeInitializer());
        this.jpaLazyGetterFieldCheck = new JpaLazyGetterFieldCheck<>(context);
    }

    @Override
    public void check() {
        var inspector = new FieldInspector<>(context.getType(), config.isKotlin());

        if (!context.getClassProbe().isEqualsInheritedFromObject()) {
            inspector.check(arrayFieldCheck);
            inspector.check(floatAndDoubleFieldCheck);
            inspector.check(reflexivityFieldCheck);
        }

        if (!ignoreMutability(context.getType())) {
            inspector.check(mutableStateFieldCheck);
        }

        if (!config.warningsToSuppress().contains(Warning.TRANSIENT_FIELDS)) {
            inspector.check(transientFieldsCheck);
        }

        inspector.check(significantFieldCheck);
        inspector.check(symmetryFieldCheck);
        inspector.check(transitivityFieldCheck);
        inspector.check(stringFieldCheck);

        if (!config.warningsToSuppress().contains(Warning.BIGDECIMAL_EQUALITY)) {
            inspector.check(bigDecimalFieldCheck);
        }

        AnnotationCache cache = config.annotationCache();
        if (cache.hasClassAnnotation(config.type(), SupportedAnnotations.ENTITY)
                && !config.warningsToSuppress().contains(Warning.JPA_GETTER)) {
            inspector.check(jpaLazyGetterFieldCheck);
        }
    }

    private boolean ignoreMutability(Class<?> type) {
        AnnotationCache cache = config.annotationCache();
        return config.warningsToSuppress().contains(Warning.NONFINAL_FIELDS)
                || cache.hasClassAnnotation(type, SupportedAnnotations.IMMUTABLE)
                || cache.hasClassAnnotation(type, SupportedAnnotations.ENTITY);
    }
}
