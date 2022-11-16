package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.util.Map;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MapEntryHashCodeRequirementChecker<T> implements Checker {

    private final Configuration<T> config;
    private final ClassAccessor<T> classAccessor;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public MapEntryHashCodeRequirementChecker(Configuration<T> config) {
        this.config = config;
        this.classAccessor = config.getClassAccessor();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        if (Map.Entry.class.isAssignableFrom(classAccessor.getType())) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) classAccessor.getRedObject(config.getTypeTag());

            int expectedHashCode = Objects.hashCode(e.getKey()) ^ Objects.hashCode(e.getValue());
            int actualHashCode = cachedHashCodeInitializer.getInitializedHashCode(e);

            Formatter f = Formatter.of(
                "Map.Entry: hashCode for\n  %%\nshould be %% but was %%.\n" +
                "The hash code of a map entry e is defined as:\n" +
                "    (e.getKey()==null ? 0 : e.getKey().hashCode()) ^ (e.getValue()==null ? 0 : e.getValue().hashCode())\n" +
                "or, using Java 8 API:\n" +
                "    java.util.Objects.hashCode(e.getKey()) ^ java.util.Objects.hashCode(e.getValue())",
                e,
                expectedHashCode,
                actualHashCode
            );
            assertEquals(f, expectedHashCode, actualHashCode);
        }
    }
}
