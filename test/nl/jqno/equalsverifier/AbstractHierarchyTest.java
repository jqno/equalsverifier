/*
 * Copyright 2009 Jan Ouwens
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

import nl.jqno.equalsverifier.points.Color;

import org.junit.Test;

public class AbstractHierarchyTest extends EqualsVerifierTestBase {
	@Test
	public void abstractFinalMethods() {
		EqualsVerifier.forClass(AbstractFinalMethodsPoint.class).verify();
	}
	
	@Test
	public void abstractRedefinable() {
		EqualsVerifier.forClass(AbstractRedefinablePoint.class)
				.withRedefinedSubclass(FinalRedefinedPoint.class)
				.verify();
	}
	
	@Test
	public void abstractNeverNull() {
		EqualsVerifier.forClass(NullThrowingColorContainer.class)
				.fieldsAreNeverNull()
				.verify();
		
		EqualsVerifier<NullThrowingColorContainer> ev = EqualsVerifier.forClass(NullThrowingColorContainer.class);
		verifyFailure("Non-nullity: equals throws NullPointerException", ev);
	}
	
	private static abstract class AbstractFinalMethodsPoint {
		private final int x;
		private final int y;
		
		AbstractFinalMethodsPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof AbstractFinalMethodsPoint)) {
				return false;
			}
			AbstractFinalMethodsPoint p = (AbstractFinalMethodsPoint)obj;
			return x == p.x && y == p.y;
		}
		
		@Override
		public final int hashCode() {
			return x + (31 * y);
		}
	}
	
	private static abstract class AbstractRedefinablePoint {
		private final int x;
		private final int y;
		
		AbstractRedefinablePoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean canEqual(Object obj) {
			return obj instanceof AbstractRedefinablePoint;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractRedefinablePoint)) {
				return false;
			}
			AbstractRedefinablePoint p = (AbstractRedefinablePoint)obj;
			return p.canEqual(this) && x == p.x && y == p.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + x + "," + y;
		}
	}
	
	private static final class FinalRedefinedPoint extends AbstractRedefinablePoint {
		private final Color color;
		
		FinalRedefinedPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean canEqual(Object obj) {
			return obj instanceof FinalRedefinedPoint;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FinalRedefinedPoint)) {
				return false;
			}
			FinalRedefinedPoint p = (FinalRedefinedPoint)obj;
			return p.canEqual(this) && super.equals(p) && color == p.color;
		}
		
		@Override
		public int hashCode() {
			return (color == null ? 0 : color.hashCode()) + (31 * super.hashCode());
		}
		
		@Override
		public String toString() {
			return super.toString() + "," + color;
		}
	}
	
	private static abstract class NullThrowingColorContainer {
		private final Color color;
		
		public NullThrowingColorContainer(Color color) {
			this.color = color;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof NullThrowingColorContainer)) {
				return false;
			}
			return color.equals(((NullThrowingColorContainer)obj).color);
		}
		
		@Override
		public final int hashCode() {
			return color.hashCode();
		}
		
		@Override
		public String toString() {
			return getClass() + ":" + color;
		}
	}
}
