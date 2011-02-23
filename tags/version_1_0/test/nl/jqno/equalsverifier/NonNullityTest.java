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
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Test;

public class NonNullityTest {
	@Test
	public void throwsNullPointerException() {
		EqualsVerifier<NullPointerExceptionBrokenPoint> ev =
				EqualsVerifier.forClass(NullPointerExceptionBrokenPoint.class);
		assertFailure(ev, "Non-nullity: NullPointerException thrown");
	}
	
	@Test
	public void nullValue() {
		EqualsVerifier<NullBrokenPoint> ev = EqualsVerifier.forClass(NullBrokenPoint.class);
		assertFailure(ev, "Non-nullity: true returned for null value");
	}
	
	static class NullPointerExceptionBrokenPoint extends Point {
		public NullPointerExceptionBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!obj.getClass().equals(getClass())) {
				return false;
			}
			return super.equals(obj);
		}
	}
	
	static class NullBrokenPoint extends Point {
		public NullBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return true;
			}
			return super.equals(obj);
		}
	}
}
