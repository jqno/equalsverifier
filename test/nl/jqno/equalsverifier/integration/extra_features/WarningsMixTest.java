/*
 * Copyright 2009-2010, 2014 Jan Ouwens
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
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;

public class WarningsMixTest extends IntegrationTestBase {
	@Test
	public void fail_whenFieldsAreNonfinalAndClassIsNonfinal_givenOnlyStrictInheritanceWarningIsSuppressed() {
		expectFailure("Mutability:");
		EqualsVerifier.forClass(MutablePoint.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void fail_whenFieldsAreNonFinalAndClassIsNonFinal_givenOnlyNonfinalFieldsWarningIsSuppressed() {
		expectFailure("Subclass:");
		EqualsVerifier.forClass(MutablePoint.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenBothStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
		EqualsVerifier.forClass(MutablePoint.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceWarningIsSuppressed() {
		expectFailureWithCause(NullPointerException.class, "Non-nullity:");
		EqualsVerifier.forClass(NeverNullColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyNullFieldsWarningIsSuppressed() {
		expectFailure("Subclass:");
		EqualsVerifier.forClass(NeverNullColorContainer.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenBothStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
		EqualsVerifier.forClass(NeverNullColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
		expectFailure("Mutability:");
		EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
		expectFailureWithCause(NullPointerException.class, "Non-nullity:");
		EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyNonfinalFieldsAndNullFieldsWarningsAreSuppressed() {
		expectFailure("Subclass:");
		EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
				.suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenAllNecessaryWarningsAreSuppressed() {
		EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	static class MutablePoint {
		private int x;
		private int y;
		
		public MutablePoint(int x, int y) { this.x = x; this.y = y; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutablePoint)) {
				return false;
			}
			MutablePoint other = (MutablePoint)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static class NeverNullColorContainer {
		private final Color color;
		
		public NeverNullColorContainer(Color color) { this.color = color; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NeverNullColorContainer)) {
				return false;
			}
			return color == ((NeverNullColorContainer)obj).color;
		}
		
		@Override
		public int hashCode() {
			return color.hashCode();
		}
	}
	
	static class NeverNullAndMutableColorContainer {
		private Color color;
		
		public NeverNullAndMutableColorContainer(Color color) { this.color = color; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NeverNullAndMutableColorContainer)) {
				return false;
			}
			return color == ((NeverNullAndMutableColorContainer)obj).color;
		}
		
		@Override
		public int hashCode() {
			return color.hashCode();
		}
	}
}
