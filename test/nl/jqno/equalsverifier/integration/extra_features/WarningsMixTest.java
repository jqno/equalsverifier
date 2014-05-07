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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;

public class WarningsMixTest {
	@Test
	public void fail_whenFieldsAreNonfinalAndClassIsNonfinal_givenOnlyStrictInheritanceWarningIsSuppressed() {
		EqualsVerifier<MutablePoint> ev1 = EqualsVerifier.forClass(MutablePoint.class);
		ev1.suppress(Warning.STRICT_INHERITANCE);
		assertFailure(ev1, "Mutability:");
	}
	
	@Test
	public void fail_whenFieldsAreNonFinalAndClassIsNonFinal_givenOnlyNonfinalFieldsWarningIsSuppressed() {
		EqualsVerifier<MutablePoint> ev2 = EqualsVerifier.forClass(MutablePoint.class);
		ev2.suppress(Warning.NONFINAL_FIELDS);
		assertFailure(ev2, "Subclass:");
	}
	
	@Test
	public void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenBothStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
		EqualsVerifier.forClass(MutablePoint.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceWarningIsSuppressed() {
		EqualsVerifier<NeverNullColorContainer> ev1 = EqualsVerifier.forClass(NeverNullColorContainer.class);
		ev1.suppress(Warning.STRICT_INHERITANCE);
		assertFailure(ev1, NullPointerException.class, "Non-nullity:");
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyNullFieldsWarningIsSuppressed() {
		EqualsVerifier<NeverNullColorContainer> ev2 = EqualsVerifier.forClass(NeverNullColorContainer.class);
		ev2.suppress(Warning.NULL_FIELDS);
		assertFailure(ev2, "Subclass:");
	}
	
	@Test
	public void succeed_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenBothStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
		EqualsVerifier.forClass(NeverNullColorContainer.class)
				.suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
		EqualsVerifier<NeverNullAndMutableColorContainer> ev1 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev1.suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS);
		assertFailure(ev1, "Mutability:");
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
		EqualsVerifier<NeverNullAndMutableColorContainer> ev2 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev2.suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS);
		assertFailure(ev2, NullPointerException.class, "Non-nullity:");
	}
	
	@Test
	public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyNonfinalFieldsAndNullFieldsWarningsAreSuppressed() {
		EqualsVerifier<NeverNullAndMutableColorContainer> ev3 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev3.suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS);
		assertFailure(ev3, "Subclass:");
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
		
		public MutablePoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public final boolean equals(Object obj) {
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
		
		public NeverNullColorContainer(Color color) {
			this.color = color;
		}
		
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
		
		public NeverNullAndMutableColorContainer(Color color) {
			this.color = color;
		}
		
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
