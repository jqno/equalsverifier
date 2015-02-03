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
 * @author Niall Gallagher
 */
public class CachedHashCodeTest extends IntegrationTestBase {
	
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
	public void fail_whenCachedHashCodeIsInvalid_givenWithCachedHashCodeIsUsed() {
		expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
		EqualsVerifier.forClass(ObjectWithInvalidCachedHashCode.class)
				.withCachedHashCode("cachedHashCode", "calcHashCode")
				.verify();
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
}
