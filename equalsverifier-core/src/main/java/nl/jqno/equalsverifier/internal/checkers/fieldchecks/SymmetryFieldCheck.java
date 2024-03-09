package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class SymmetryFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public SymmetryFieldCheck(SubjectCreator<T> subjectCreator) {
        this.subjectCreator = subjectCreator;
    }

    @Override
    public void execute(Field changedField) {
        T left = subjectCreator.plain();
        T right = subjectCreator.plain();
        T changedLeft = subjectCreator.withFieldChanged(changedField);
        T changedRight = subjectCreator.withFieldChanged(changedField);

        checkSymmetry(left, right);
        checkSymmetry(left, changedRight);
        checkSymmetry(changedLeft, changedRight);
    }

    private void checkSymmetry(T left, T right) {
        assertTrue(
            Formatter.of("Symmetry: objects are not symmetric:\n  %%\nand\n  %%", left, right),
            left.equals(right) == right.equals(left)
        );
    }
}
