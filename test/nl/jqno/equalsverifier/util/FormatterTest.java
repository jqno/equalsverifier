package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FormatterTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void noParameters() {
		Formatter f = Formatter.of("No parameters");
		assertEquals("No parameters", f.format());
	}
	
	@Test
	public void oneSimpleParameter() {
		Formatter f = Formatter.of("One simple parameter: %%", new Simple(42));
		assertEquals("One simple parameter: Simple: 42", f.format());
	}
	
	@Test
	public void multipleSimpleParameters() {
		Formatter f = Formatter.of("Multiple simple parameters: %% and %% and also %%", new Simple(0), new Simple(1), new Simple(2));
		assertEquals("Multiple simple parameters: Simple: 0 and Simple: 1 and also Simple: 2", f.format());
	}
	
	@Test
	public void oneThrowingParameter() {
		Formatter f = Formatter.of("One throwing parameter: %%", new Throwing(1337, "string"));
		assertEquals("One throwing parameter: [Throwing i=1337 s=string]-throws IllegalStateException(msg)", f.format());
	}
	
	@Test
	public void oneThrowingParameterWithNullSubparameter() {
		Formatter f = Formatter.of("One throwing parameter: %%", new Throwing(1337, null));
		assertEquals("One throwing parameter: [Throwing i=1337 s=null]-throws IllegalStateException(msg)", f.format());
	}
	
	@Test
	public void oneParameterWithNoFieldsAndThrowsWithNullMessage() {
		Formatter f = Formatter.of("No fields, null message: %%", new NoFieldsAndThrowsNullMessage());
		assertEquals("No fields, null message: [NoFieldsAndThrowsNullMessage]-throws NullPointerException(null)", f.format());
	}
	
	@Test
	public void oneAbstractParameter() {
		Instantiator<Abstract> i = Instantiator.of(Abstract.class);
		Formatter f = Formatter.of("Abstract: %%", i.instantiate());
		assertThat(f.format(), containsString("Abstract: [FormatterTest$Abstract x=0]-throws AbstractMethodError"));
	}
	
	@Test
	public void oneConcreteSubclassParameter() {
		Instantiator<AbstractImpl> i = Instantiator.of(AbstractImpl.class);
		Formatter f = Formatter.of("Concrete: %%", i.instantiate());
		assertThat(f.format(), containsString("Concrete: something concrete"));
	}
	
	@Test
	public void oneDelegatedAbstractParameter() {
		Instantiator<AbstractDelegation> i = Instantiator.of(AbstractDelegation.class);
		Formatter f = Formatter.of("Abstract: %%", i.instantiate());
		assertThat(f.format(), containsString("Abstract: [FormatterTest$AbstractDelegation y=0]-throws AbstractMethodError"));
	}
	
	@Test
	public void oneDelegatedConcreteSubclassParameter() {
		Instantiator<AbstractDelegationImpl> i = Instantiator.of(AbstractDelegationImpl.class);
		Formatter f = Formatter.of("Concrete: %%", i.instantiate());
		assertThat(f.format(), containsString("Concrete: something concrete"));
	}
	
	@Test
	public void nullParameter() {
		Formatter f = Formatter.of("This parameter is null: %%", (Object)null);
		assertEquals("This parameter is null: null", f.format());
	}
	
	@Test
	public void nullMessage() {
		thrown.expect(NullPointerException.class);
		
		Formatter.of(null);
	}
	
	@Test
	public void notEnoughParameters() {
		Formatter f = Formatter.of("Not enough: %% and %%");
		
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Not enough parameters");
		
		f.format();
	}
	
	@Test
	public void tooManyParameters() {
		Formatter f = Formatter.of("Too many!", new Simple(0));
		
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Too many parameters");
		
		f.format();
	}
	
	static class Simple {
		private final int i;
		
		public Simple(int i) {
			this.i = i;
		}
		
		@Override
		public String toString() {
			return "Simple: " + i;
		}
	}
	
	static class Throwing {
		@SuppressWarnings("unused")
		private final int i;
		@SuppressWarnings("unused")
		private final String s;
		
		public Throwing(int i, String s) {
			this.i = i;
			this.s = s;
		}
		
		@Override
		public String toString() {
			throw new IllegalStateException("msg");
		}
	}
	
	static class NoFieldsAndThrowsNullMessage {
		@Override
		public String toString() {
			throw new NullPointerException();
		}
	}
	
	static abstract class Abstract {
		@SuppressWarnings("unused")
		private final int x = 10;
		
		@Override
		public abstract String toString();
	}
	
	static class AbstractImpl extends Abstract {
		@Override
		public String toString() {
			return "something concrete";
		}
	}
	
	static abstract class AbstractDelegation {
		@SuppressWarnings("unused")
		private final int y = 20;
		
		public abstract String somethingAbstract();
		
		@Override
		public String toString() {
			return somethingAbstract();
		}
	}
	
	static class AbstractDelegationImpl extends AbstractDelegation {
		@Override
		public String somethingAbstract() {
			return "something concrete";
		}
	}
}
