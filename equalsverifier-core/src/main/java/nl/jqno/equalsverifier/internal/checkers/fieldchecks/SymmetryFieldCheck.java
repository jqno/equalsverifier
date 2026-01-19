package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class SymmetryFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;

    public SymmetryFieldCheck(SubjectCreator<T> subjectCreator) {
        this.subjectCreator = subjectCreator;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        T left = subjectCreator.plain();
        T right = subjectCreator.plain();
        T changedRight = subjectCreator.withFieldChanged(fieldProbe.getField());

        checkSymmetry(left, right);
        checkSymmetry(left, changedRight);
    }

    private void checkSymmetry(T left, T right) {
        assertTrue(
            Formatter.of("Symmetry: objects are not symmetric:\n  %%\nand\n  %%", left, right),
            left.equals(right) == right.equals(left));
    }
}
