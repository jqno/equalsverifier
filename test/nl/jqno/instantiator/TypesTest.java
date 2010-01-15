package nl.jqno.instantiator;

import org.junit.Test;

public class TypesTest {
	@Test
	public void instantiateTypes() {
		Instantiator<TypesContainer> instantiator = Instantiator.forClass(TypesContainer.class);
		TypesContainer cc = instantiator.instantiate();
		instantiator.scramble(cc);
	}
	
	static class TypesContainer {
		Class<?> klass;
		Object object;
	}
}
