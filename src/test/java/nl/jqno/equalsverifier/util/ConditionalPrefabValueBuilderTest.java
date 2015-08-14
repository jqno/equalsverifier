/*
 * Copyright 2014-2015 Jan Ouwens
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

import nl.jqno.equalsverifier.util.exceptions.EqualsVerifierBugException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static nl.jqno.equalsverifier.util.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.util.ConditionalInstantiator.objects;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConditionalPrefabValueBuilderTest {
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
	public void throwsBug_whenNoInstancesAreCreated() {
		thrown.expect(EqualsVerifierBugException.class);
		ConditionalPrefabValueBuilder.of(GregorianCalendar.class.getCanonicalName())
				.addTo(prefabValues);
	}
	
	@Test
	public void throwsBug_whenOnlyOneInstanceIsCreated() {
		thrown.expect(EqualsVerifierBugException.class);
		ConditionalPrefabValueBuilder.of(GregorianCalendar.class.getCanonicalName())
				.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31))
				.addTo(prefabValues);
	}
	
	@Test
	public void prefabValuesContainsInstances_whenValidInstantiationParametersAreProvided() {
		ConditionalPrefabValueBuilder.of(GregorianCalendar.class.getCanonicalName())
				.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31))
				.instantiate(classes(int.class, int.class, int.class), objects(2009, 5, 1))
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(GregorianCalendar.class), is(new GregorianCalendar(1999, 11, 31)));
		assertThat(prefabValues.getBlack(GregorianCalendar.class), is(new GregorianCalendar(2009, 5, 1)));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenConstructorParameters() {
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31))
				.instantiate(classes(int.class, int.class, int.class), objects(2009, 5, 1))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenNonExistingConstructorOverloadIsCalled() {
		ConditionalPrefabValueBuilder.of(GregorianCalendar.class.getCanonicalName())
				.instantiate(classes(int.class, String.class, int.class), objects(1999, "11", 31))
				.instantiate(classes(int.class, int.class, int.class), objects(2009, 5, 1))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenInstantiateIsCalledMoreThanTwice() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(GregorianCalendar.class.getCanonicalName())
				.instantiate(classes(int.class, int.class, int.class), objects(1999, 11, 31))
				.instantiate(classes(int.class, int.class, int.class), objects(2009, 5, 1));
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.instantiate(classes(int.class, int.class, int.class), objects(2014, 6, 16));
	}
	
	@Test
	public void prefabValuesContainsInstances_whenPrefabValuesAreProvided() {
		prefabValues.put(String.class, "red", "black");
		
		ConditionalPrefabValueBuilder.of(StringsContainer.class.getCanonicalName())
				.instantiate(classes(String.class, String.class), prefabValues)
				.instantiate(classes(String.class, String.class), prefabValues)
				.addTo(prefabValues);
		
		StringsContainer red = prefabValues.getRed(StringsContainer.class);
		StringsContainer black = prefabValues.getBlack(StringsContainer.class);
		assertNotNull(red);
		assertNotNull(black);
		assertNotEquals(red, black);
	}
	
	@Test
	public void throwsBug_whenRequiredPrefabValuesAreNotAvailable() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(StringsContainer.class.getCanonicalName());
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.instantiate(classes(String.class, String.class), prefabValues);
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenConstructorWithPrefabValues() {
		prefabValues.put(String.class, "red", "black");
		
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.instantiate(classes(String.class, String.class), prefabValues)
				.instantiate(classes(String.class, String.class), prefabValues)
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	@Test
	public void nothingHappens_whenNonExistingConstructorOverloadIsCalled_givenPrefabValues() {
		prefabValues.put(String.class, "red", "black");
		
		ConditionalPrefabValueBuilder.of(StringsContainer.class.getCanonicalName())
				.instantiate(classes(String.class, String.class), prefabValues)
				.instantiate(classes(String.class), prefabValues)
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenInstantiateIsCalledMoreThanTwice_givenPrefabValues() {
		prefabValues.put(String.class, "red", "black");
		
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(StringsContainer.class.getCanonicalName())
				.instantiate(classes(String.class, String.class), prefabValues)
				.instantiate(classes(String.class, String.class), prefabValues);
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.instantiate(classes(String.class, String.class), prefabValues);
	}
	
	@Test
	public void prefabValuesContainsInstances_whenValidFactoryParametersAreProvided() {
		ConditionalPrefabValueBuilder.of(Integer.class.getCanonicalName())
				.callFactory("valueOf", classes(int.class), objects(42))
				.callFactory("valueOf", classes(int.class), objects(1337))
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(Integer.class), is(Integer.valueOf(42)));
		assertThat(prefabValues.getBlack(Integer.class), is(1337));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenFactoryParameters() {
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.callFactory("valueOf", classes(int.class), objects(42))
				.callFactory("valueOf", classes(int.class), objects(1337))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenFactoryMethodDoesNotExist() {
		ConditionalPrefabValueBuilder.of(Integer.class.getCanonicalName())
				.callFactory("thisFactoryMethodDoesNotExist", classes(int.class), objects(42))
				.callFactory("valueOf", classes(int.class), objects(1337))
				.addTo(prefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenNonExistingFactoryOverloadIsCalled() {
		ConditionalPrefabValueBuilder.of(Integer.class.getCanonicalName())
				.callFactory("valueOf", classes(int.class), objects(42))
				.callFactory("valueOf", classes(String.class), objects("hi"))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenCallFactoryIsCalledMoreThanTwice() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(Integer.class.getCanonicalName())
				.callFactory("valueOf", classes(int.class), objects(42))
				.callFactory("valueOf", classes(int.class), objects(1337));
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.callFactory("valueOf", classes(int.class), objects(-1));
	}
	
	@Test
	public void prefabValuesContainsInstances_whenValidExternalFactoryParametersAreProvided() {
		ConditionalPrefabValueBuilder.of(List.class.getCanonicalName())
				.callFactory("java.util.Collections", "emptyList", classes(), objects())
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1))
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(List.class), is((List)Collections.emptyList()));
		assertThat(prefabValues.getBlack(List.class), is((List)Collections.singletonList(1)));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenExternalFactoryParameters() {
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.callFactory("java.util.Collections", "emptyList", classes(), objects())
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenExternalFactoryClassDoesNotExist() {
		ConditionalPrefabValueBuilder.of(List.class.getCanonicalName())
				.callFactory("java.util.ThisTypeDoesNotExist", "emptyList", classes(), objects())
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1))
				.addTo(prefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenExternalFactoryMethodDoesNotExist() {
		ConditionalPrefabValueBuilder.of(List.class.getCanonicalName())
				.callFactory("java.util.Collections", "thisFactoryMethodDoesNotExist", classes(), objects())
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1))
				.addTo(prefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenNonExistingExternalFactoryOverloadIsCalled() {
		ConditionalPrefabValueBuilder.of(List.class.getCanonicalName())
				.callFactory("java.util.Collections", "emptyList", classes(Object.class), objects(1))
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenCallExternalFactoryIsCalledMoreThanTwice() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(List.class.getCanonicalName())
				.callFactory("java.util.Collections", "emptyList", classes(), objects())
				.callFactory("java.util.Collections", "singletonList", classes(Object.class), objects(1));
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.callFactory("java.util.Collections", "emptyList", classes(), objects());
	}
	
	@Test
	public void prefabValuesContainsInstances_whenValidConstantsAreProvided() {
		ConditionalPrefabValueBuilder.of(BigDecimal.class.getCanonicalName())
				.withConstant("ONE")
				.withConstant("TEN")
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(BigDecimal.class), is(BigDecimal.ONE));
		assertThat(prefabValues.getBlack(BigDecimal.class), is(BigDecimal.TEN));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenValidConstants() {
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.withConstant("ONE")
				.withConstant("TEN")
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenConstantDoesNotExist() {
		ConditionalPrefabValueBuilder.of(BigDecimal.class.getCanonicalName())
				.withConstant("ONE")
				.withConstant("ELEVENTY_TWELVE")
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenWithConstantIsCalledMoreThanTwice() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(BigDecimal.class.getCanonicalName())
				.withConstant("ONE")
				.withConstant("TEN");
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.withConstant("ZERO");
	}
	
	@Test
	public void prefabValuesContainsInstances_whenTypeIsInterface_givenAConcreteImplementation() {
		ConditionalPrefabValueBuilder.of("nl.jqno.equalsverifier.util.ConditionalInterface")
				.withConcreteClass("nl.jqno.equalsverifier.util.ConditionalConcreteClass")
				.instantiate(classes(int.class), objects(42))
				.instantiate(classes(int.class), objects(1337))
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(ConditionalInterface.class), is((ConditionalInterface)new ConditionalConcreteClass(42)));
		assertThat(prefabValues.getBlack(ConditionalInterface.class), is((ConditionalInterface)new ConditionalConcreteClass(1337)));
	}
	
	@Test
	public void prefabValuesContainsInstances_whenTypeIsAbstract_givenAConcreteImplementation() {
		ConditionalPrefabValueBuilder.of("nl.jqno.equalsverifier.util.ConditionalAbstractClass")
				.withConcreteClass("nl.jqno.equalsverifier.util.ConditionalConcreteClass")
				.instantiate(classes(int.class), objects(42))
				.instantiate(classes(int.class), objects(1337))
				.addTo(prefabValues);
		
		assertThat(prefabValues.getRed(ConditionalAbstractClass.class), is((ConditionalAbstractClass)new ConditionalConcreteClass(42)));
		assertThat(prefabValues.getBlack(ConditionalAbstractClass.class), is((ConditionalAbstractClass)new ConditionalConcreteClass(1337)));
	}
	
	@Test
	public void nothingHappens_whenTypeDoesNotExist_givenAConcreteImplementation() {
		ConditionalPrefabValueBuilder.of("this.type.does.not.exist")
				.withConcreteClass("java.lang.String")
				.instantiate(classes(String.class), objects("1"))
				.instantiate(classes(String.class), objects("2"))
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void nothingHappens_whenConcreteClassDoesNotExist() {
		ConditionalPrefabValueBuilder.of("java.util.List")
				.withConcreteClass("this.type.does.not.exist")
				.instantiate(classes(), objects())
				.instantiate(classes(), objects())
				.addTo(throwingPrefabValues);
		
		throwingPrefabValues.verify();
	}
	
	@Test
	public void throwsBug_whenConcreteClassIsNotASubclassOfType() {
		ConditionalPrefabValueBuilder builder = ConditionalPrefabValueBuilder.of(BigDecimal.class.getCanonicalName());
		
		thrown.expect(EqualsVerifierBugException.class);
		builder.withConcreteClass(String.class.getCanonicalName());
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
	
interface ConditionalInterface {}
abstract class ConditionalAbstractClass implements ConditionalInterface {}
final class ConditionalConcreteClass extends ConditionalAbstractClass {
	@SuppressWarnings("unused") private final int i;
	public ConditionalConcreteClass(int i) { this.i = i; }
	@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
	@Override public int hashCode() { return defaultHashCode(this); }
}

final class StringsContainer {
	@SuppressWarnings("unused") private final String s;
	@SuppressWarnings("unused") private final String t;
	public StringsContainer(String s, String t) { this.s = s; this.t = t; }
	@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
	@Override public int hashCode() { return defaultHashCode(this); }
}

