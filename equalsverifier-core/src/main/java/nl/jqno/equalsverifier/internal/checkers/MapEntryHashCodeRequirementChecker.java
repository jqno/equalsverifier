package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.util.Map.Entry;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class MapEntryHashCodeRequirementChecker<T> implements Checker {

    private final Configuration<T> config;
    private final ClassAccessor<T> classAccessor;

    public MapEntryHashCodeRequirementChecker(Configuration<T> config) {
        this.config = config;
        this.classAccessor = config.getClassAccessor();
    }

    @Override
    public void check() {
        if (config.getWarningsToSuppress().contains(null)) { // FIXME dedicated warning?
            return;
        }

        if (Entry.class.isAssignableFrom(classAccessor.getType())) {
            Entry<?, ?> e = (Entry<?, ?>) classAccessor.getRedObject(config.getTypeTag());

            int hashCode = (e.getKey() == null ? 0 : e.getKey().hashCode()) ^ (e.getValue() == null ? 0 : e.getValue().hashCode());

            // FIXME better message
            Formatter f = Formatter.of("hashCode: value does not follow Map.Entry specification");
            assertTrue(f, e.hashCode() == hashCode);
        }
    }

}
