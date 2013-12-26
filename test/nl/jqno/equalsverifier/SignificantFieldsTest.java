/*
 * Copyright 2009-2010, 2012-2013 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
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
	public void extraFieldInEquals() {
		EqualsVerifier<ExtraFieldInEqualsPoint> ev = EqualsVerifier.forClass(ExtraFieldInEqualsPoint.class);
		assertFailure(ev, "Significant fields", "equals relies on", "yNotUsed", "but hashCode does not");
	}
	
	@Test
	public void extraFieldInHashCode() {
		EqualsVerifier<ExtraFieldInHashCodePoint> ev = EqualsVerifier.forClass(ExtraFieldInHashCodePoint.class);
		assertFailure(ev, "Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
	}
	
	@Test
	public void allFieldsUsed() {
		EqualsVerifier.forClass(FinalPoint.class).allFieldsShouldBeUsed().verify();
	}
	
	@Test
	public void oneFieldUnused() {
		EqualsVerifier.forClass(OneFieldUnusedColorPoint.class).verify();
		
		EqualsVerifier<OneFieldUnusedColorPoint> ev = EqualsVerifier.forClass(OneFieldUnusedColorPoint.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "equals does not use", "colorNotUsed");
	}
	
	@Test
	public void oneTransientFieldUnused() {
		EqualsVerifier.forClass(OneTransientFieldUnusedColorPoint.class).allFieldsShouldBeUsed().verify();
	}
	
	@Test
	public void oneStaticFieldUnused() {
		EqualsVerifier.forClass(OneStaticFieldUnusedColorPoint.class).allFieldsShouldBeUsed().verify();
	}
	
	@Test
	public void oneFieldUnusedExtended() {
		EqualsVerifier.forClass(OneFieldUnusedExtendedColorPoint.class).verify();
		
		EqualsVerifier<OneFieldUnusedExtendedColorPoint> ev = EqualsVerifier.forClass(OneFieldUnusedExtendedColorPoint.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "equals does not use", "colorNotUsed");
	}
	
	@Test
	public void noFieldsUsed() {
		EqualsVerifier.forClass(NoFieldsUsed.class).verify();
		
		EqualsVerifier<NoFieldsUsed> ev = EqualsVerifier.forClass(NoFieldsUsed.class);
		ev.allFieldsShouldBeUsed();
		assertFailure(ev, "Significant fields", "all fields should be used", "NoFieldsUsed", "has not defined an equals method");
	}
	
	@Test
	public void allFieldsUsedExceptOne() {
		EqualsVerifier.forClass(OneFieldUnusedColorPoint.class)
				.allFieldsShouldBeUsedExcept("colorNotUsed")
				.verify();
	}
	
	@Test
	public void allFieldsUsedExceptTwo() {
		EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class)
				.allFieldsShouldBeUsedExcept("colorNotUsed", "colorAlsoNotUsed")
				.verify();
	}
	
	@Test
	public void allFieldsShouldBeUsedExceptOneButAnotherIsAlsoNotUsed() {
		EqualsVerifier<TwoFieldsUnusedColorPoint> ev = EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class);
		ev.allFieldsShouldBeUsedExcept("colorNotUsed");
		assertFailure(ev, "Significant fields", "equals does not use", "colorAlsoNotUsed");
	}
	
	@Test
	public void allFieldsShouldBeUsedExceptOneThatIsActuallyUsed() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forClass(FinalPoint.class);
		ev.allFieldsShouldBeUsedExcept("x");
		assertFailure(ev, "Significant fields", "equals should not use", "x", "but it does");
	}
	
	@Test
	public void allFieldsShouldBeUsedExceptTwoButOneOfThemIsActuallyUsed() {
		EqualsVerifier<OneFieldUnusedColorPoint> ev = EqualsVerifier.forClass(OneFieldUnusedColorPoint.class);
		ev.allFieldsShouldBeUsedExcept("x", "colorNotUsed");
		assertFailure(ev, "Significant fields", "equals should not use", "x", "but it does");
	}
	
	@Test
	public void allFieldsShouldBeUsedExceptOneThatDoesNotExist() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forClass(FinalPoint.class);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Class FinalPoint does not contain field thisFieldDoesNotExist.");

		ev.allFieldsShouldBeUsedExcept("thisFieldDoesNotExist");
	}
	
	@Test
	public void skipStaticFinalFields() {
		EqualsVerifier.forClass(IndirectStaticFinalContainer.class).verify();
	}
	
	static final class ExtraFieldInEqualsPoint {
		private final int x;
		private final int yNotUsed;
		
		public ExtraFieldInEqualsPoint(int x, int y) {
			this.x = x;
			this.yNotUsed = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ExtraFieldInEqualsPoint)) {
				return false;
			}
			ExtraFieldInEqualsPoint p = (ExtraFieldInEqualsPoint)obj;
			return p.x == x && p.yNotUsed == yNotUsed;
		}
		
		@Override
		public int hashCode() {
			return x;
		}
	}
	
	static final class ExtraFieldInHashCodePoint {
		private final int x;
		private final int yNotUsed;

		public ExtraFieldInHashCodePoint(int x, int y) {
			this.x = x;
			this.yNotUsed = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ExtraFieldInHashCodePoint)) {
				return false;
			}
			ExtraFieldInHashCodePoint p = (ExtraFieldInHashCodePoint)obj;
			return p.x == x;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * yNotUsed);
		}
	}
	
	static final class OneFieldUnusedColorPoint {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private final Color colorNotUsed;
		
		public OneFieldUnusedColorPoint(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.colorNotUsed = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OneFieldUnusedColorPoint)) {
				return false;
			}
			OneFieldUnusedColorPoint other = (OneFieldUnusedColorPoint)obj;
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
	
	static final class OneFieldUnusedExtendedColorPoint extends Point {
		@SuppressWarnings("unused")
		private final Color colorNotUsed;
		
		public OneFieldUnusedExtendedColorPoint(int x, int y, Color color) {
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
