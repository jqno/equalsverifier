/*
 * Copyright 2012 Jan Ouwens
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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.MutableStateTest.MutableIntContainer;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.ObjectAccessor;

import org.junit.Test;

public class OriginalStateTest {
	private static final Object INSTANCE_1 = new Object();
	private static final Object INSTANCE_2 = new Object();
	private static final Object STATIC = new Object();
	private static final Object STATIC_FINAL = new Object();
	
	@Test
	public void staticValueReturnsToOriginalState() {
		EqualsVerifier.forClass(CorrectEquals.class).verify();
		assertEquals(STATIC_FINAL, CorrectEquals.staticFinalValue);
		assertEquals(STATIC, CorrectEquals.staticValue);
	}
	
	@Test
	public void instanceValueReturnsToOriginalState() {
		CorrectEquals one = new CorrectEquals(INSTANCE_1);
		CorrectEquals two = new CorrectEquals(INSTANCE_2);
		EqualsVerifier.forExamples(one, two).verify();
		
		assertEquals(INSTANCE_1, one.instanceValue);
		assertEquals(INSTANCE_2, two.instanceValue);
	}
	
	@Test
	public void staticValueReturnsToOriginalStateRecursively() {
		EqualsVerifier.forClass(CorrectEqualsContainer.class).verify();
		assertEquals(STATIC, CorrectEquals.staticValue);
	}
	
	@Test
	public void valuesReturnToOriginalStateAfterException() throws NoSuchFieldException {
		EqualsVerifier<MutableIntContainer> ev = EqualsVerifier.forClass(MutableIntContainer.class);
		MockStaticFieldValueStash<MutableIntContainer> stash = new MockStaticFieldValueStash<MutableIntContainer>(MutableIntContainer.class);

		// Mock EqualsVerifier's StaticFieldValueStash
		ObjectAccessor<EqualsVerifier<MutableIntContainer>> objectAccessor = ObjectAccessor.of(ev);
		FieldAccessor stashAccessor = objectAccessor.fieldAccessorFor(EqualsVerifier.class.getDeclaredField("stash"));
		stashAccessor.set(stash);

		// Make sure the exception actually occurs, on a check that actually mutates the fields.
		assertFailure(ev, "Mutability");

		// Assert
		assertTrue(stash.restoreCalled);
	}
	
	private static class MockStaticFieldValueStash<T> extends StaticFieldValueStash<T> {
		boolean restoreCalled = false;
		
		public MockStaticFieldValueStash(Class<T> type) {
			super(type);
		}
		
		@Override
		public void restore() {
			restoreCalled  = true;
			super.restore();
		}
	}
	
	static final class CorrectEquals {
		private static final Object staticFinalValue = STATIC_FINAL;
		private static Object staticValue = STATIC;
		private final Object instanceValue;
		
		public CorrectEquals(Object instanceValue) {
			this.instanceValue = instanceValue;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CorrectEquals)) {
				return false;
			}
			CorrectEquals other = (CorrectEquals)obj;
			return nullSafeEquals(instanceValue, other.instanceValue);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(instanceValue);
		}
	}
	
	static final class CorrectEqualsContainer {
		private final CorrectEquals foo;
		
		public CorrectEqualsContainer(CorrectEquals foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CorrectEqualsContainer)) {
				return false;
			}
			CorrectEqualsContainer other = (CorrectEqualsContainer)obj;
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(foo);
		}
	}
}
