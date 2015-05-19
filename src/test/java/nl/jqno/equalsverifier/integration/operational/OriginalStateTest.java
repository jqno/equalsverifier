/*
 * Copyright 2012, 2014-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.operational;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.Configuration;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.MockStaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.PrefabValuesFactory;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.ObjectAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;

import org.junit.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class OriginalStateTest extends IntegrationTestBase {
	private static final Object INSTANCE_1 = new Object();
	private static final Object INSTANCE_2 = new Object();
	private static final Object STATIC = new Object();
	private static final Object STATIC_FINAL = new Object();
	
	@Test
	public void staticValueReturnsToOriginalState_whenEqualsVerifierIsFinished() {
		EqualsVerifier.forClass(CorrectEquals.class).verify();
		assertEquals(STATIC_FINAL, CorrectEquals.staticFinalValue);
		assertEquals(STATIC, CorrectEquals.staticValue);
	}
	
	@Test
	public void instanceValueReturnsToOriginalState_whenEqualsVerifierIsFinished_givenForExamplesIsUsed() {
		CorrectEquals one = new CorrectEquals(INSTANCE_1);
		CorrectEquals two = new CorrectEquals(INSTANCE_2);
		EqualsVerifier.forExamples(one, two).verify();
		
		assertEquals(INSTANCE_1, one.instanceValue);
		assertEquals(INSTANCE_2, two.instanceValue);
	}
	
	@Test
	public void staticValueReturnsToOriginalStateRecursively_whenEqualsVerifierIsFinished() {
		EqualsVerifier.forClass(CorrectEqualsContainer.class).verify();
		assertEquals(STATIC, CorrectEquals.staticValue);
	}
	
	@Test
	public void staticValueReturnsToOriginalStateDeeplyRecursively_whenEqualsVerifierIsFinished() {
		EqualsVerifier.forClass(CorrectEqualsContainerContainer.class).verify();
		assertEquals(STATIC, CorrectEquals.staticValue);
	}
	
	@Test
	public void staticValueInSuperReturnsToOriginalState_whenEqualsVerifierIsFinished() {
		EqualsVerifier.forClass(SubContainer.class).verify();
		assertEquals(STATIC, CorrectEquals.staticValue);
		assertEquals(STATIC, SuperContainer.staticValue);
		assertEquals(STATIC_FINAL, SuperContainer.staticFinalValue);
	}
	
	@Test
	public void allValuesReturnToOriginalState_whenEqualsVerifierIsFinishedWithException() throws NoSuchFieldException {
		EqualsVerifier<MutableIntContainer> ev = EqualsVerifier.forClass(MutableIntContainer.class);
		MockStaticFieldValueStash mockStash = new MockStaticFieldValueStash();
		PrefabValues mockPrefabValues = PrefabValuesFactory.withPrimitives(mockStash);
		
		// Mock EqualsVerifier's StaticFieldValueStash
		ObjectAccessor<?> objectAccessor = ObjectAccessor.of(ev);
		FieldAccessor configFieldAccessor = objectAccessor.fieldAccessorFor(EqualsVerifier.class.getDeclaredField("config"));
		ObjectAccessor<?> configAccessor = ObjectAccessor.of(configFieldAccessor.get());
		FieldAccessor prefabValuesAccessor = configAccessor.fieldAccessorFor(Configuration.class.getDeclaredField("prefabValues"));
		prefabValuesAccessor.set(mockPrefabValues);
		
		// Make sure the exception actually occurs, on a check that actually mutates the fields.
		expectFailure("Mutability");
		ev.verify();
		
		// Assert
		assertTrue(mockStash.restoreCalled);
	}
	
	static final class CorrectEquals {
		private static final Object staticFinalValue = STATIC_FINAL;
		private static Object staticValue = STATIC;
		private final Object instanceValue;
		
		public CorrectEquals(Object instanceValue) { this.instanceValue = instanceValue; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	static final class CorrectEqualsContainer {
		private final CorrectEquals foo;
		
		public CorrectEqualsContainer(CorrectEquals foo) { this.foo = foo; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	static final class CorrectEqualsContainerContainer {
		private final CorrectEqualsContainer foo;
		
		public CorrectEqualsContainerContainer(CorrectEqualsContainer foo) { this.foo = foo; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	static abstract class SuperContainer {
		private static final Object staticFinalValue = STATIC_FINAL;
		private static Object staticValue = STATIC;
		
		private final CorrectEquals foo;
		
		public SuperContainer(CorrectEquals foo) { this.foo = foo; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SuperContainer)) {
				return false;
			}
			SuperContainer other = (SuperContainer)obj;
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}

	static final class SubContainer extends SuperContainer {
		public SubContainer(CorrectEquals foo) {
			super(foo);
		}
	}
	
	static final class MutableIntContainer {
		private int field;
		
		public MutableIntContainer(int value) { field = value; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
