package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.util.function.Predicate;

import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MutableStateFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final Predicate<FieldProbe> isCachedHashCodeField;

    public MutableStateFieldCheck(SubjectCreator<T> subjectCreator, Predicate<FieldProbe> isCachedHashCodeField) {
        this.subjectCreator = subjectCreator;
        this.isCachedHashCodeField = isCachedHashCodeField;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (isCachedHashCodeField.test(fieldProbe)) {
            return;
        }

        T reference = subjectCreator.plain();
        T copy = subjectCreator.plain();

        boolean equalBefore = reference.equals(copy);
        T changed = subjectCreator.withFieldChanged(fieldProbe.getField());
        boolean equalAfter = reference.equals(changed);

        if (equalBefore && !equalAfter && !fieldProbe.isFinal()) {
            String message = """
                             Mutability: equals depends on mutable field %%.
                             Make the field final, suppress Warning.NONFINAL_FIELDS or use EqualsVerifier.simple()""";
            fail(Formatter.of(message, fieldProbe.getName()));
        }
    }
}
