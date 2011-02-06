/*
 * Copyright 2009-2010 Jan Ouwens
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
