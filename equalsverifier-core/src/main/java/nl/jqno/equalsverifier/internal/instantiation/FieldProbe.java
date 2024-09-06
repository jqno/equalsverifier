package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;

public final class FieldProbe {

    private final Field field;
    private final boolean isWarningZeroSuppressed;
    private final boolean isWarningNullSuppressed;
    private final Set<String> nonnullFields;
    private final AnnotationCache annotationCache;

    private FieldProbe(Field field, Configuration<?> config) {
        this.field = field;
        this.isWarningZeroSuppressed = config.getWarningsToSuppress().contains(Warning.ZERO_FIELDS);
        this.isWarningNullSuppressed = config.getWarningsToSuppress().contains(Warning.NULL_FIELDS);
        this.nonnullFields = config.getNonnullFields();
        this.annotationCache = config.getAnnotationCache();
    }

    public static FieldProbe of(Field field, Configuration<?> config) {
        return new FieldProbe(field, config);
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Can't defensively copy a Field.")
    public Field getField() {
        return field;
    }

    @SuppressFBWarnings(
        value = "DP_DO_INSIDE_DO_PRIVILEGED",
        justification = "Only called in test code, not production."
    )
    public Object getValue(Object object) {
        field.setAccessible(true);
        return rethrow(() -> field.get(object));
    }

    public Class<?> getType() {
        return field.getType();
    }

    public String getName() {
        return field.getName();
    }

    public boolean fieldIsPrimitive() {
        return getType().isPrimitive();
    }

    public boolean fieldIsStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean canBeDefault() {
        if (fieldIsPrimitive()) {
            return !isWarningZeroSuppressed;
        }

        boolean isAnnotated = NonnullAnnotationVerifier.fieldIsNonnull(field, annotationCache);
        boolean isMentionedExplicitly = nonnullFields.contains(field.getName());
        return !isWarningNullSuppressed && !isAnnotated && !isMentionedExplicitly;
    }
}
