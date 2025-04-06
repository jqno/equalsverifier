package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class TransitivityFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "PrefabValues is inherently mutable.")
    public TransitivityFieldCheck(SubjectCreator<T> subjectCreator) {
        this.subjectCreator = subjectCreator;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        T a1 = subjectCreator.plain();
        T b1 = subjectCreator.withFieldChanged(fieldProbe.getField());
        T b2 = subjectCreator.withAllFieldsChanged();

        boolean x = a1.equals(b1);
        boolean y = b1.equals(b2);
        boolean z = a1.equals(b2);

        if (countFalses(x, y, z) == 1) {
            fail(
                Formatter
                        .of(
                            """
                            Transitivity: two of these three instances are equal to each other, so the third one should be, too:
                            -  %%
                            -  %%
                            -  %%""",
                            a1,
                            b1,
                            b2));
        }
    }

    private int countFalses(boolean... bools) {
        int result = 0;
        for (boolean b : bools) {
            if (!b) {
                result++;
            }
        }
        return result;
    }
}
