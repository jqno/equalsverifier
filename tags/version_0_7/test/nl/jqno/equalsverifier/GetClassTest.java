/*
 * Copyright 2010 Jan Ouwens
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

import static nl.jqno.equalsverifier.Helper.assertFailure;
import static nl.jqno.equalsverifier.Helper.nullSafeHashCode;
import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.FinalMethodsPoint;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class GetClassTest {
	@Test
	public void happyPath() {
		EqualsVerifier.forClass(GetClassPointHappyPath.class)
				.usingGetClass()
				.verify();
	}
	
	@Test
	public void nullCheckForgotten() {
		EqualsVerifier<GetClassPointNull> ev =
				EqualsVerifier.forClass(GetClassPointNull.class).usingGetClass();
		assertFailure(ev, "Non-nullity: NullPointerException thrown");
	}
	
	@Test
	public void useInstanceofWhenGetClassAnnounced() {
		EqualsVerifier.forClass(FinalMethodsPoint.class)
				.verify();
		
		EqualsVerifier<FinalMethodsPoint> ev =
			EqualsVerifier.forClass(FinalMethodsPoint.class).usingGetClass();
		assertFailure(ev, "Subclass", "object is equal to an instance of a trivial subclass with equal fields", "This should not happen when using getClass().");
	}
	
	@Test
	public void redefinedSubclassHappyPath() {
		EqualsVerifier.forClass(GetClassColorPoint.class)
				.usingGetClass()
				.verify();
	}
	
	@Test
	public void equalSuperclass() {
		EqualsVerifier<GetClassColorPointWithEqualSuper> ev =
				EqualsVerifier.forClass(GetClassColorPointWithEqualSuper.class).usingGetClass();
		assertFailure(ev, "Redefined superclass", GetClassColorPointWithEqualSuper.class.getSimpleName(),
				"may not equal superclass instance", Point.class.getSimpleName(), "but it does");
	}
	
	static class GetClassPointHappyPath {
		private final int x;
		private final int y;
		
		GetClassPointHappyPath(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			GetClassPointHappyPath p = (GetClassPointHappyPath)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static class GetClassPointNull {
		private final int x;
		private final int y;
		
		GetClassPointNull(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj.getClass() != getClass()) {
				return false;
			}
			GetClassPointNull p = (GetClassPointNull)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static class GetClassColorPoint extends GetClassPointHappyPath {
		private final Color color;
		
		public GetClassColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (super.equals(obj)) {
				GetClassColorPoint other = (GetClassColorPoint)obj;
				return color == other.color;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode() + nullSafeHashCode(color);
		}
	}
	
	static class GetClassColorPointWithEqualSuper extends Point {
		private final Color color;
		
		public GetClassColorPointWithEqualSuper(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			GetClassColorPointWithEqualSuper p = (GetClassColorPointWithEqualSuper)obj;
			return super.equals(obj) && color == p.color;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode() + (31 * nullSafeHashCode(color));
		}
	}
}
