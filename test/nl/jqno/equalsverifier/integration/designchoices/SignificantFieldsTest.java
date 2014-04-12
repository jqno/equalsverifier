/*
 * Copyright 2009-2010, 2012-2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.designchoices;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.points.Color;
import nl.jqno.equalsverifier.testhelpers.points.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SignificantFieldsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void fail_whenEqualsUsesAFieldAndHashCodeDoesnt() {
		EqualsVerifier<ExtraFieldInEquals> ev = EqualsVerifier.forClass(ExtraFieldInEquals.class);
		assertFailure(ev, "Significant fields", "equals relies on", "yNotUsed", "but hashCode does not");
	}
	
	@Test
	public void fail_whenHashCodeUsesAFieldAndEqualsDoesnt() {
		EqualsVerifier<ExtraFieldInHashCode> ev = EqualsVerifier.forClass(ExtraFieldInHashCode.class);
		assertFailure(ev, "Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
	}
	
	@Test
	public void succeed_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsed() {
		EqualsVerifier.forClass(FinalPoint.class)
				.allFieldsShouldBeUsed()
				.verify();
	}
	
	@Test
	public void succeed_whenAFieldIsUnused() {
		EqualsVerifier.forClass(OneFieldUnused.class).verify();
	}
	
	@Test
	public void fail_whenAFieldIsUnused_givenAllFieldsShouldBeUsed() {
		EqualsVerifier<OneFieldUnused> ev = EqualsVerifier.forClass(OneFieldUnused.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "equals does not use", "colorNotUsed");
	}
	
	@Test
	public void succeed_whenATransientFieldIsUnused_givenAllFieldsShouldBeUsed() {
		EqualsVerifier.forClass(OneTransientFieldUnusedColorPoint.class)
				.allFieldsShouldBeUsed()
				.verify();
	}
	
	@Test
	public void succeed_whenAStaticFieldIsUnused_givenAllFieldsShouldBeUsed() {
		EqualsVerifier.forClass(OneStaticFieldUnusedColorPoint.class)
				.allFieldsShouldBeUsed()
				.verify();
	}
	
	@Test
	public void succeed_whenAFieldIsUnusedInASubclass() {
		EqualsVerifier.forClass(OneFieldUnusedExtended.class).verify();
	}
	
	@Test
	public void fail_whenAFieldIsUnusedInASubclass_givenAllFieldsShouldBeUsed() {
		EqualsVerifier<OneFieldUnusedExtended> ev = EqualsVerifier.forClass(OneFieldUnusedExtended.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "equals does not use", "colorNotUsed");
	}
	
	@Test
	public void succeed_whenNoFieldsAreUsed() {
		EqualsVerifier.forClass(NoFieldsUsed.class).verify();
	}
	
	@Test
	public void fail_whenNoFieldsAreUsed_givenAllFieldsShouldBeUsed() {
		EqualsVerifier<NoFieldsUsed> ev = EqualsVerifier.forClass(NoFieldsUsed.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "all fields should be used", "NoFieldsUsed", "has not defined an equals method");
	}
	
	@Test
	public void succeed_whenAFieldIsUnused_givenAllFieldsShouldBeUsedExceptThatField() {
		EqualsVerifier.forClass(OneFieldUnused.class)
				.allFieldsShouldBeUsedExcept("colorNotUsed")
				.verify();
	}
	
	@Test
	public void succeed_whenTwoFieldsAreUnused_givenAllFieldsShouldBeUsedExceptThoseTwo() {
		EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class)
				.allFieldsShouldBeUsedExcept("colorNotUsed", "colorAlsoNotUsed")
				.verify();
	}
	
	@Test
	public void fail_whenTwoFieldsAreUnUsed_givenAllFieldsShouldBeUsedExceptOneOfThemButNotBoth() {
		EqualsVerifier<TwoFieldsUnusedColorPoint> ev = EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class);
		ev.allFieldsShouldBeUsedExcept("colorNotUsed");
		assertFailure(ev, "Significant fields", "equals does not use", "colorAlsoNotUsed");
	}
	
	@Test
	public void fail_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsedExceptOneThatActuallyIsUsed() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forClass(FinalPoint.class);
		ev.allFieldsShouldBeUsedExcept("x");
		assertFailure(ev, "Significant fields", "equals should not use", "x", "but it does");
	}
	
	@Test
	public void fail_whenOneFieldIsUnused_givenAllFieldsShouldBeUsedExceptTwoFields() {
		EqualsVerifier<OneFieldUnused> ev = EqualsVerifier.forClass(OneFieldUnused.class);
		ev.allFieldsShouldBeUsedExcept("x", "colorNotUsed");
		assertFailure(ev, "Significant fields", "equals should not use", "x", "but it does");
	}
	
	@Test
	public void anExceptionIsThrown_whenANonExistingFieldIsExcepted() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forClass(FinalPoint.class);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Class FinalPoint does not contain field thisFieldDoesNotExist.");
		
		ev.allFieldsShouldBeUsedExcept("thisFieldDoesNotExist");
	}
	
	@Test
	public void succeed_whenAUsedFieldHasUnusedStaticFinalMembers() {
		EqualsVerifier.forClass(IndirectStaticFinalContainer.class).verify();
	}
	
	static final class ExtraFieldInEquals {
		private final int x;
		private final int yNotUsed;
		
		public ExtraFieldInEquals(int x, int y) {
			this.x = x;
			this.yNotUsed = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ExtraFieldInEquals)) {
				return false;
			}
			ExtraFieldInEquals p = (ExtraFieldInEquals)obj;
			return p.x == x && p.yNotUsed == yNotUsed;
		}
		
		@Override
		public int hashCode() {
			return x;
		}
	}
	
	static final class ExtraFieldInHashCode {
		private final int x;
		private final int yNotUsed;
		
		public ExtraFieldInHashCode(int x, int y) {
			this.x = x;
			this.yNotUsed = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ExtraFieldInHashCode)) {
				return false;
			}
			ExtraFieldInHashCode p = (ExtraFieldInHashCode)obj;
			return p.x == x;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * yNotUsed);
		}
	}
	
	static final class OneFieldUnused {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private final Color colorNotUsed;
		
		public OneFieldUnused(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.colorNotUsed = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OneFieldUnused)) {
				return false;
			}
			OneFieldUnused other = (OneFieldUnused)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static final class OneTransientFieldUnusedColorPoint {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private transient final Color color;
		
		public OneTransientFieldUnusedColorPoint(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OneTransientFieldUnusedColorPoint)) {
				return false;
			}
			OneTransientFieldUnusedColorPoint other = (OneTransientFieldUnusedColorPoint)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static final class OneStaticFieldUnusedColorPoint {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private static Color color;
		
		public OneStaticFieldUnusedColorPoint(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			OneStaticFieldUnusedColorPoint.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OneStaticFieldUnusedColorPoint)) {
				return false;
			}
			OneStaticFieldUnusedColorPoint other = (OneStaticFieldUnusedColorPoint)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static final class OneFieldUnusedExtended extends Point {
		@SuppressWarnings("unused")
		private final Color colorNotUsed;
		
		public OneFieldUnusedExtended(int x, int y, Color color) {
			super(x, y);
			this.colorNotUsed = color;
		}
	}
	
	static final class NoFieldsUsed {
		@SuppressWarnings("unused")
		private final Color color;
		
		public NoFieldsUsed(Color color) {
			this.color = color;
		}
	}
	
	static final class TwoFieldsUnusedColorPoint {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private final Color colorNotUsed;
		@SuppressWarnings("unused")
		private final Color colorAlsoNotUsed;
		
		public TwoFieldsUnusedColorPoint(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.colorNotUsed = color;
			this.colorAlsoNotUsed = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsUnusedColorPoint)) {
				return false;
			}
			TwoFieldsUnusedColorPoint other = (TwoFieldsUnusedColorPoint)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static final class X {
		public static final X x = new X();
	}
	
	static final class IndirectStaticFinalContainer {
		private final X x;
		
		public IndirectStaticFinalContainer(X x) {
			this.x = x;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof IndirectStaticFinalContainer)) {
				return false;
			}
			return ((IndirectStaticFinalContainer)obj).x == x;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(x);
		}
	}
}
