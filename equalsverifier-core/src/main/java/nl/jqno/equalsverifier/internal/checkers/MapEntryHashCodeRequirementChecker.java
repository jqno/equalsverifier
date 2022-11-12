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
        if (config.getWarningsToSuppress().contains(null)) { // FIXME dedicated warning?
            return;
        }

        if (Map.Entry.class.isAssignableFrom(classAccessor.getType())) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) classAccessor.getRedObject(config.getTypeTag());

            int expectedHashCode = Objects.hashCode(e.getKey()) ^ Objects.hashCode(e.getValue());
            int actualHashCode = cachedHashCodeInitializer.getInitializedHashCode(e);

            // FIXME better message
            Formatter f = Formatter.of("hashCode: value does not follow Map.Entry specification");
            assertEquals(f, expectedHashCode, actualHashCode);
        }
    }
}
