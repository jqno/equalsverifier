package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.util.Map;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MapEntryHashCodeRequirementChecker<T> implements Checker {

    private final Configuration<T> config;
    private final ValueProvider valueProvider;

    public MapEntryHashCodeRequirementChecker(Context<T> context) {
        this.config = context.getConfiguration();
        this.valueProvider = context.getValueProvider();
    }

    @Override
    public void check() {
        if (Map.Entry.class.isAssignableFrom(config.getType())) {
            Map.Entry<?, ?> e = valueProvider.<Map.Entry<?, ?>>provideOrThrow(config.getTypeTag()).getRed();

            int expectedHashCode = Objects.hashCode(e.getKey()) ^ Objects.hashCode(e.getValue());
            int actualHashCode = config.getCachedHashCodeInitializer().getInitializedHashCode(e);

            Formatter f = Formatter
                    .of(
                        "Map.Entry: hashCode for\n  %%\nshould be %% but was %%.\n"
                                + "The hash code of a map entry e is defined as:\n"
                                + "    (e.getKey()==null ? 0 : e.getKey().hashCode()) ^ (e.getValue()==null ? 0 : e.getValue().hashCode())\n"
                                + "or, using Java 8 API:\n"
                                + "    java.util.Objects.hashCode(e.getKey()) ^ java.util.Objects.hashCode(e.getValue())",
                        e,
                        expectedHashCode,
                        actualHashCode);
            assertEquals(f, expectedHashCode, actualHashCode);
        }
    }
}
