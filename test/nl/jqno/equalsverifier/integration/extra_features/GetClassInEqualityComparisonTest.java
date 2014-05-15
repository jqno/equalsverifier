/*
 * Copyright 2010, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class GetClassInEqualityComparisonTest extends IntegrationTestBase {
	@Test
	public void succeed_whenGetClassIsPartOfEqualityComparison_givenAnAbstractSuperclassAndUsingGetClassIsUsed() {
		EqualsVerifier.forClass(Identifiable.class)
				.usingGetClass()
				.verify();
	}
	
	@Test
	public void succeed_whenGetClassIsPartOfEqualityComparison_givenAConcreteImplementationAndUsingGetClassIsUsed() {
		EqualsVerifier.forClass(Person.class)
				.usingGetClass()
				.verify();
	}
	
	@Test
	public void succeed_whenGetClassIsPartOfEqualityComparison_givenAnotherConcreteImplementationAndUsingGetClassIsUsed() {
		EqualsVerifier.forClass(Account.class)
				.usingGetClass()
				.verify();
	}
	
	static abstract class Identifiable {
		private final int id;
		
		public Identifiable(int id) { this.id = id; }
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof Identifiable)) {
				return false;
			}
			Identifiable other = (Identifiable)obj;
			return id == other.id && getClass() == other.getClass();
		}
		
		@Override
		public final int hashCode() {
			return id;
		}
	}
	
	static class Person extends Identifiable {
		public Person(int id) {
			super(id);
		}
	}
	
	static class Account extends Identifiable {
		public Account(int id) {
			super(id);
		}
	}
}
