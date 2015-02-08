package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.util.Assert.assertEquals;
import nl.jqno.equalsverifier.util.Formatter;

public class CachedHashCodeChecker<T> implements Checker {
	private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
	
	public CachedHashCodeChecker(CachedHashCodeInitializer<T> cachedHashCodeInitializer) {
		this.cachedHashCodeInitializer = cachedHashCodeInitializer;
	}
	
	@Override
	public void check() {
		if (cachedHashCodeInitializer.isPassthrough()) {
			return;
		}
		
		T reference = cachedHashCodeInitializer.getExample();
		int actualHashCode = reference.hashCode();
		int recomputedHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);
		assertEquals(Formatter.of("cachedHashCode is not properly initialized."), actualHashCode, recomputedHashCode);
	}
}
