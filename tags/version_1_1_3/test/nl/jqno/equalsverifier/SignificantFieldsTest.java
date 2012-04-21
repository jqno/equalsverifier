/*
 * Copyright 2009-2010, 2012 Jan Ouwens
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

import org.junit.Test;

public class SignificantFieldsTest {
	@Test
	public void extraFieldInEquals() {
		EqualsVerifier<ExtraFieldInEqualsPoint> ev = EqualsVerifier.forClass(ExtraFieldInEqualsPoint.class);
		assertFailure(ev, "Significant fields", "equals relies on", "but hashCode does not");
	}
	
	@Test
	public void extraFieldInHashCode() {
		EqualsVerifier<ExtraFieldInHashCodePoint> ev = EqualsVerifier.forClass(ExtraFieldInHashCodePoint.class);
		assertFailure(ev, "Significant fields", "hashCode relies on", "but equals does not");
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
		assertFailure(ev, "Significant fields", "equals does not use");
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
		assertFailure(ev, "Significant fields", "equals does not use");
	}
	
	@Test
	public void skipStaticFinalFields() {
		EqualsVerifier.forClass(IndirectStaticFinalContainer.class).verify();
	}
	
	static final class ExtraFieldInEqualsPoint {
		private final int x;
		private final int y;
		
		public ExtraFieldInEqualsPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ExtraFieldInEqualsPoint)) {
				return false;
			}
			ExtraFieldInEqualsPoint p = (ExtraFieldInEqualsPoint)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x;
		}
	}
	
	static final class ExtraFieldInHashCodePoint {
		private final int x;
		private final int y;

		public ExtraFieldInHashCodePoint(int x, int y) {
			this.x = x;
			this.y = y;
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
			return x + (31 * y);
		}
	}
	
	static final class OneFieldUnusedColorPoint {
		private final int x;
		private final int y;
		@SuppressWarnings("unused")
		private final Color color;
		
		public OneFieldUnusedColorPoint(int x, int y, Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
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
		private final Color color;
		
		public OneFieldUnusedExtendedColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
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
