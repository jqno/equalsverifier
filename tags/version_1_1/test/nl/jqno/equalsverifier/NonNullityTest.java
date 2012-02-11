/*
 * Copyright 2009-2011 Jan Ouwens
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
	
	@Test
	public void typeCheckOmitted() {
		EqualsVerifier<TypeCheckBroken> ev = EqualsVerifier.forClass(TypeCheckBroken.class);
		assertFailure(ev, "Type-check: equals throws ClassCastException");
	}
	
	@Test
	public void typeCheckOmittedIllegalState() {
		EqualsVerifier<TypeCheckBrokenIllegalState> ev = EqualsVerifier.forClass(TypeCheckBrokenIllegalState.class);
		assertFailure(ev, "Type-check: equals throws IllegalStateException");
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
	
	static class TypeCheckBroken {
		private int i;
		
		public TypeCheckBroken(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			return i == ((TypeCheckBroken)obj).i;
		}
	}
	
	static class TypeCheckBrokenIllegalState {
		private int i;
		
		public TypeCheckBrokenIllegalState(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			try {
				if (obj == null) {
					return false;
				}
				return i == ((TypeCheckBrokenIllegalState)obj).i;
			}
			catch (ClassCastException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
