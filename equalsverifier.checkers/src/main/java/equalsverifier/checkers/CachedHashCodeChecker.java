package equalsverifier.checkers;

import equalsverifier.utils.CachedHashCodeInitializer;
import equalsverifier.utils.Configuration;
import equalsverifier.utils.Formatter;
import equalsverifier.utils.Warning;

import java.util.EnumSet;

import static equalsverifier.utils.Assert.*;

public class CachedHashCodeChecker<T> implements Checker {
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final EnumSet<Warning> warningsToSuppress;

    public CachedHashCodeChecker(Configuration<T> config) {
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
        this.warningsToSuppress = config.getWarningsToSuppress();
    }

    @Override
    public void check() {
        if (cachedHashCodeInitializer.isPassthrough()) {
            return;
        }
        if (warningsToSuppress.contains(Warning.NONFINAL_FIELDS)) {
            fail(Formatter.of("Cached hashCode: EqualsVerifier can only check cached hashCodes for immutable classes."));
        }

        T reference = cachedHashCodeInitializer.getExample();
        if (warningsToSuppress.contains(Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE)) {
            if (reference != null) {
                fail(Formatter.of("Cached hashCode: example must be null if %% is suppressed", Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE.name()));
            }
        }
        else {
            if (reference == null) {
                fail(Formatter.of("Cached hashCode: example cannot be null."));
            }
            int actualHashCode = reference.hashCode();
            int recomputedHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);

            assertEquals(Formatter.of("Cached hashCode: hashCode is not properly initialized."), actualHashCode, recomputedHashCode);
            assertFalse(Formatter.of("Cached hashCode: example.hashCode() cannot be zero. Please choose a different example."),
                    actualHashCode == 0);
        }
    }
}
