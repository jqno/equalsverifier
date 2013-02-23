/*
 * Copyright 2013 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	public void oneThrowingContainerParameter() {
		Instantiator<Throwing> i = Instantiator.of(Throwing.class);
		ThrowingContainer tc = new ThrowingContainer(i.instantiate());
		Formatter f = Formatter.of("TC: %%", tc);
		assertThat(f.format(), containsString("TC: [ThrowingContainer t=[Throwing i=0 s=null]-throws IllegalStateException(msg)]-throws IllegalStateException(msg)"));
	}
	
	@Test
	public void oneAbstractContainerParameter() {
		Instantiator<AbstractDelegation> i = Instantiator.of(AbstractDelegation.class);
		AbstractContainer ac = new AbstractContainer(i.instantiate());
		Formatter f = Formatter.of("AC: %%", ac);
		assertThat(f.format(), containsString("AC: [AbstractContainer ad=[FormatterTest$AbstractDelegation y=0]-throws AbstractMethodError(null)]-throws AbstractMethodError(null)"));
	}
	
	@Test
	public void parameterWithMixOfVariousFields() {
		Instantiator<Abstract> i = Instantiator.of(Abstract.class);
		Mix mix = new Mix();
		mix.ab = i.instantiate();
		
		Formatter f = Formatter.of("%%", mix);
		assertThat(f.format(), containsString("[Mix i=42 s=null t=not null ab=[FormatterTest$Abstract x=0]-throws AbstractMethodError(null)]-throws UnsupportedOperationException(null)"));
	}
	
	@Test
	public void connectedParameters() {
		Formatter f = Formatter.of("%%%%", 1, 2);
		assertThat(f.format(), containsString("12"));
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
	
	static class ThrowingContainer {
		private final Throwing t;
		
		public ThrowingContainer(Throwing t) {
			this.t = t;
		}
		
		@Override
		public String toString() {
			return "ThrowingContainer " + t.toString();
		}
	}
	
	static class AbstractContainer {
		public final AbstractDelegation ad;
		
		public AbstractContainer(AbstractDelegation ad) {
			this.ad = ad;
		}
		
		@Override
		public String toString() {
			return "AbstractContainer: " + ad.toString();
		}
	}
	
	static class Mix {
		public final int i = 42;
		public final String s = null;
		public final String t = "not null";
		public Abstract ab;
		
		@Override
		public String toString() {
			throw new UnsupportedOperationException();
		}
	}
}
