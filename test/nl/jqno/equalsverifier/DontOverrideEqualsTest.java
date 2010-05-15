package nl.jqno.equalsverifier;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class DontOverrideEqualsTest {
	@Test
	public void pojosDontOverrideEquals() {
		EqualsVerifier.forClass(Pojo.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	public final class Pojo {
		private String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return getClass().getName() + " " + value;
		}
	}
}
