package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class StringFieldCheck<T> implements FieldCheck<T> {

    public static final String ERROR_DOC_TITLE = "String equality";

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public StringFieldCheck(
        SubjectCreator<T> subjectCreator,
        ValueProvider instanceCreator,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer
    ) {
        this.subjectCreator = subjectCreator;
        this.valueProvider = instanceCreator;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_CONVERT_CASE",
        justification = "String prefab values are probably not localized."
    )
    public void execute(FieldProbe fieldProbe) {
        if (String.class.equals(fieldProbe.getType()) && !fieldProbe.isStatic()) {
            TypeTag string = new TypeTag(String.class);
            String red = valueProvider.<String>provide(string).getRed();

            final T reference;
            final T copy;
            try {
                reference = subjectCreator.withFieldSetTo(fieldProbe.getField(), red.toLowerCase());
                copy = subjectCreator.withFieldSetTo(fieldProbe.getField(), red.toUpperCase());
            } catch (ReflectionException ignored) {
                // Differently-cased String is not allowed, so cannot cause problems either.
                return;
            }

            boolean theyAreEqual = reference.equals(copy);
            boolean theirHashCodesAreEqual =
                cachedHashCodeInitializer.getInitializedHashCode(reference) ==
                cachedHashCodeInitializer.getInitializedHashCode(copy);

            if (theyAreEqual && !theirHashCodesAreEqual) {
                fail(
                    Formatter.of(
                        ERROR_DOC_TITLE +
                        ": class uses equalsIgnoreCase to compare String field %%, but hashCode is case-sensitive." +
                        " Use toUpperCase() to determine the hashCode.",
                        fieldProbe.getName()
                    )
                );
            }
        }
    }
}
