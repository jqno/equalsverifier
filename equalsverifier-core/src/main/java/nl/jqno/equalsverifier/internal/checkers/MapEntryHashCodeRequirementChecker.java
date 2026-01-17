package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.util.Map;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class MapEntryHashCodeRequirementChecker<T> implements Checker {

    private final Configuration<T> config;
    private final SubjectCreator<T> subjectCreator;

    public MapEntryHashCodeRequirementChecker(Context<T> context) {
        this.config = context.getConfiguration();
        this.subjectCreator = context.getSubjectCreator();
    }

    @Override
    public void check() {
        if (Map.Entry.class.isAssignableFrom(config.type())) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) subjectCreator.plain();

            int expectedHashCode = Objects.hashCode(e.getKey()) ^ Objects.hashCode(e.getValue());
            int actualHashCode = config.cachedHashCodeInitializer().getInitializedHashCode(e);

            Formatter f = Formatter
                    .of(
                        """
                        Map.Entry: hashCode for
                          %%
                        should be %% but was %%.
                        The hash code of a map entry e is defined as:
                            (e.getKey()==null ? 0 : e.getKey().hashCode()) ^ (e.getValue()==null ? 0 : e.getValue().hashCode())
                        or, using Java 8 API:
                            java.util.Objects.hashCode(e.getKey()) ^ java.util.Objects.hashCode(e.getValue())""",
                        e,
                        expectedHashCode,
                        actualHashCode);
            assertEquals(f, expectedHashCode, actualHashCode);
        }
    }
}
