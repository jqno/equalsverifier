package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MutableStateFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final Predicate<FieldAccessor> isCachedHashCodeField;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public MutableStateFieldCheck(
        SubjectCreator<T> subjectCreator,
        Predicate<FieldAccessor> isCachedHashCodeField
    ) {
        this.subjectCreator = subjectCreator;
        this.isCachedHashCodeField = isCachedHashCodeField;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        FieldAccessor fieldAccessor = FieldAccessor.of(fieldProbe.getField());
        if (isCachedHashCodeField.test(fieldAccessor)) {
            return;
        }

        T reference = subjectCreator.plain();
        T copy = subjectCreator.plain();

        boolean equalBefore = reference.equals(copy);
        T changed = subjectCreator.withFieldChanged(fieldProbe.getField());
        boolean equalAfter = reference.equals(changed);

        if (equalBefore && !equalAfter && !fieldAccessor.fieldIsFinal()) {
            String message =
                "Mutability: equals depends on mutable field %%.\n" +
                "Make the field final, suppress Warning.NONFINAL_FIELDS or use" +
                " EqualsVerifier.simple()";
            fail(Formatter.of(message, fieldProbe.getName()));
        }
    }
}
