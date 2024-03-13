package nl.jqno.equalsverifier.internal.checkers;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.NullPointerExceptionFieldCheck;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.util.Configuration;

public class NullChecker<T> implements Checker {

    private final Configuration<T> config;

    public NullChecker(Configuration<T> config) {
        this.config = config;
    }

    @Override
    public void check() {
        if (config.getWarningsToSuppress().contains(Warning.NULL_FIELDS)) {
            return;
        }

        FieldInspector<T> inspector = new FieldInspector<>(config.getType(), config);
        inspector.check(new NullPointerExceptionFieldCheck<>(new SubjectCreator<>(config), config));
    }
}
