/*
 * Copyright 2015 Jan Ouwens
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

import javax.annotation.Nonnull;

/**
 * @author Niall Gallagher, Jan Ouwens
 */
public class CachedHashCodeTest extends IntegrationTestBase {
	private static final String MALFORMED_CALCULATEHASHCODEMETHOD = "Could not find calculateHashCodeMethod: must be private int";

	@Test
	public void fail_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsNotUsed() {
		expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
		EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
				.verify();
	}
	
	@Test
	public void succeed_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsUsed() {
		EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
				.withCachedHashCode("cachedHashCode", "calcHashCode")
				.verify();
	}
	
	@Test
	public void succeed_whenCachedHashCodeIsValidAndLocatedInSuperclass_givenWithCachedHashCodeIsUsed() {
		EqualsVerifier.forClass(Subclass.class)
				.withCachedHashCode("cachedHashCode", "calcHashCode")
				.verify();
	}
	
	@Test
	public void fail_whenCachedHashCodeIsInvalid_givenWithCachedHashCodeIsUsed() {
		expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
		EqualsVerifier.forClass(ObjectWithInvalidCachedHashCode.class)
				.withCachedHashCode("cachedHashCode", "calcHashCode")
				.verify();
	}
	
	@Test
	public void fail_whenCachedHashCodeFieldDoesNotExist() {
		expectException(IllegalArgumentException.class, "Could not find cachedHashCodeField", "doesNotExist");
		EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
				.withCachedHashCode("doesNotExist", "calcHashCode");
	}
	
	@Test
	public void fail_whenCalculateHashCodeMethodDoesNotExist() {
		expectException(IllegalArgumentException.class, "Could not find calculateHashCodeMethod", "doesNotExist");
		EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
				.withCachedHashCode("cachedHashCode", "doesNotExist");
	}
	
	@Test
	public void fail_whenCalculateHashCodeMethodIsNotPrivate() {
		expectException(IllegalArgumentException.class, MALFORMED_CALCULATEHASHCODEMETHOD, "notPrivate");
		EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
				.withCachedHashCode("cachedHashCode", "notPrivate");
	}
	
	@Test
	public void fail_whenCalculateHashCodeMethodDoesNotReturnInt() {
		expectException(IllegalArgumentException.class, MALFORMED_CALCULATEHASHCODEMETHOD, "notAnInt");
		EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
				.withCachedHashCode("cachedHashCode", "notAnInt");
	}
	
	@Test
	public void fail_whenCalculateHashCodeMethodTakesParamters() {
		expectException(IllegalArgumentException.class, MALFORMED_CALCULATEHASHCODEMETHOD, "takesParameters");
		EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
				.withCachedHashCode("cachedHashCode", "takesParameters");
	}
	
	static class ObjectWithValidCachedHashCode {
		@Nonnull private final String name;
		private final int cachedHashCode;
		
		public ObjectWithValidCachedHashCode(String name) {
			this.name = name;
			this.cachedHashCode = calcHashCode();
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof ObjectWithValidCachedHashCode)) {
				return false;
			}
			ObjectWithValidCachedHashCode that = (ObjectWithValidCachedHashCode) obj;
			return name.equals(that.name);
		}
		
		@Override
		public final int hashCode() {
			return cachedHashCode;
		}
		
		private int calcHashCode() {
			return name.hashCode();
		}
	}
	
	static class ObjectWithInvalidCachedHashCode {
		@Nonnull private final String name;
		private final int cachedHashCode;
		
		public ObjectWithInvalidCachedHashCode(String name) {
			this.name = name;
			this.cachedHashCode = calcHashCode();
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof ObjectWithInvalidCachedHashCode)) {
				return false;
			}
			ObjectWithInvalidCachedHashCode that = (ObjectWithInvalidCachedHashCode) obj;
			return name.equals(that.name);
		}
		
		@Override
		public final int hashCode() {
			return cachedHashCode;
		}
		
		private int calcHashCode() {
			return 3;
		}
	}
	
	@SuppressWarnings("unused")
	static class InvalidCalculateHashCodeMethodsContainer {
		private int cachedHashCode;
		
		public int notPrivate() { return -1; }
		private String notAnInt() { return "wrong"; }
		private int takesParameters(int x) { return x; }
	}
	
	static final class Subclass extends ObjectWithValidCachedHashCode {
		public Subclass(String name) { super(name); }
	}
}
