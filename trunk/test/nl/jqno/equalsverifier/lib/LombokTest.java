package nl.jqno.equalsverifier.lib;

import static nl.jqno.equalsverifier.Helper.assertAssertionError;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class LombokTest {
	@Test
	public void finalClass() {
		EqualsVerifier.forClass(LombokFinalPoint.class)
				.verify();
	}
	
	@Test
	public void nonFinalClassNormal() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				EqualsVerifier.forClass(LombokSuperPoint.class)
						.verify();
			}
		};
		assertAssertionError(r, "Subclass", "object is not equal to an instance of a trivial subclass with equal fields");
	}

	@Test
	public void nonFinalClassSuppressInhertianceWarning() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				EqualsVerifier.forClass(LombokSuperPoint.class)
						.suppress(Warning.STRICT_INHERITANCE)
						.verify();
			}
		};
		assertAssertionError(r, "Subclass", "object is not equal to an instance of a trivial subclass with equal fields");
	}

	@Test
	public void callSuper() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				EqualsVerifier.forClass(LombokSubPoint.class)
						.verify();
			}
		};
		assertAssertionError(r, "Symmetry", "does not equal superclass instance");
	}
	
	@Data
	public static final class LombokFinalPoint {
		private final int x;
		private final int y;
	}
	
	@Data
	public static class LombokSuperPoint {
		private final int x;
		private final int y;
	}
	
	@EqualsAndHashCode(callSuper=true)
	public static final class LombokSubPoint extends LombokSuperPoint {
		private final int z;
		
		public LombokSubPoint(int x, int y, int z) {
			super(x, y);
			this.z = z;
		}
	}
}
