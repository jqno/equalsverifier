/*
 * Copyright 2014 Jan Ouwens
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import nl.jqno.equalsverifier.StaticFieldValueStash;
import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PrefabValueBuilderTest {
	private PrefabValues prefabValues;
	private PrefabValuesThrowsWhenCalled throwingPrefabValues;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		StaticFieldValueStash stash = new StaticFieldValueStash();
		prefabValues = new PrefabValues(stash);
		throwingPrefabValues = new PrefabValuesThrowsWhenCalled();
	}

	@Test
	public void prefabValuesContainsInstances_whenValidConstantsAreProvided() {
		PrefabValueBuilder.of(BigDecimal.class.getCanonicalName())
				.withConstant("ONE")
				.withConstant("TEN")
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(BigDecimal.class), is(BigDecimal.ONE));
		assertThat(prefabValues.getBlack(BigDecimal.class), is(BigDecimal.TEN));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist() {
		PrefabValueBuilder.of("this.type.does.not.exist")
				.withConstant("ONE")
				.withConstant("TEN")
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsISE_whenWithConstantIsCalledMoreThanTwice() {
		PrefabValueBuilder builder = PrefabValueBuilder.of(BigDecimal.class.getCanonicalName())
				.withConstant("ONE")
				.withConstant("TEN");
		
		thrown.expect(ReflectionException.class);
		builder.withConstant("ZERO");
	}
	
	private static class PrefabValuesThrowsWhenCalled extends PrefabValues {
		private boolean putIsCalled = false;
		
		public PrefabValuesThrowsWhenCalled() { super(null); }
		
		@Override
		public <T> void put(Class<T> type, T red, T black) {
			putIsCalled = true;
		}
		
		public void verify() {
			if (putIsCalled) {
				throw new AssertionError("PrefabValues.put is called for non-existing type!");
			}
		}
	}
}
