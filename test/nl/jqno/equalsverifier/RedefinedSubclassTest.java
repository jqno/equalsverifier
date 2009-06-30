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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.ColorBlindColorPoint;
import nl.jqno.equalsverifier.redefinablepoint.EqualSubclassForRedefinablePoint;
import nl.jqno.equalsverifier.redefinablepoint.RedefinableColorPoint;
import nl.jqno.equalsverifier.redefinablepoint.RedefinablePoint;

import org.junit.Test;

public class RedefinedSubclassTest extends EqualsVerifierTestBase {
	@Test
	public void referenceEqualsRedefinedSub() {
		EqualsVerifier<RedefinablePoint> ev = EqualsVerifier.forClass(RedefinablePoint.class)
				.withRedefinedSubclass(EqualSubclassForRedefinablePoint.class);
		verifyFailure("Subclass: RedefinablePoint:1,1 equals EqualSubclassForRedefinablePoint:1,1.", ev);
	}
	
	@Test
	public void sanityEqualsIsValidForSuper() {
		EqualsVerifier.forClass(RedefinablePoint.class)
				.withRedefinedSubclass(RedefinableColorPoint.class)
				.verify();
	}
	
	@Test
	public void withRedefinedSuperclass() {
		EqualsVerifier.forClass(RedefinableColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
	@Test
	public void invalidWithRedefinedSuperclass() {
		EqualsVerifier<ColorBlindColorPoint> ev = EqualsVerifier.forClass(ColorBlindColorPoint.class);
		ev.withRedefinedSuperclass();
		verifyFailure("Redefined superclass: ColorBlindColorPoint:1,1,YELLOW may not equal Point:1,1, but it does.", ev);
	}
	
	@Test
	public void equalsMethodFinalSoNoRedefinedSubclassNecessary() {
		EqualsVerifier<RedeFinalPoint> ev = EqualsVerifier.forClass(RedeFinalPoint.class)
				.withRedefinedSubclass(RedeFinalSubPoint.class);
		verifyFailure("Subclass: RedeFinalPoint has a final equals method; don't need to supply a redefined subclass.", ev);
	}
	
	@Test
	public void abstractClass() {
		EqualsVerifier.forClass(AbstractRedefinablePoint.class)
				.withRedefinedSubclass(SubclassForAbstractRedefinablePoint.class)
				.verify();
	}
	
	@Test
	public void weakInheritanceBeforeRedefinedSubclass() {
		try {
			EqualsVerifier.forClass(RedefinablePoint.class)
					.weakInheritanceCheck()
					.withRedefinedSubclass(EqualSubclassForRedefinablePoint.class);
			fail("Assertion didn't fail");
		}
		catch (AssertionError e) {
			assertEquals("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.",
					e.getMessage());
		}
	}
	
	@Test
	public void weakInheritanceAfterRedefinedSubclass() {
		try {
			EqualsVerifier.forClass(RedefinablePoint.class)
			.withRedefinedSubclass(EqualSubclassForRedefinablePoint.class)
			.weakInheritanceCheck();
			fail("Assertion didn't fail");
		}
		catch (AssertionError e) {
			assertEquals("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.",
					e.getMessage());
		}
	}
	
	private static class RedeFinalPoint {
		private final int x;
		private final int y;

		public RedeFinalPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof RedeFinalPoint)) {
				return false;
			}
			RedeFinalPoint p = (RedeFinalPoint)obj;
			return x == p.x && y == p.y;
		}
		
		@Override
		public final int hashCode() {
			return x + (31 * y);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + x;
		}
	}
	
	private static final class RedeFinalSubPoint extends RedeFinalPoint {
		public RedeFinalSubPoint(int x, int y) {
			super(x, y);
		}
	}
	
	private static abstract class AbstractRedefinablePoint {
		private final int x;
		private final int y;

		public AbstractRedefinablePoint(int x, int y) {
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
			return p.canEqual(this) && p.x == x && p.y == y;
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
	
	private static final class SubclassForAbstractRedefinablePoint extends AbstractRedefinablePoint {
		private final Color color;

		SubclassForAbstractRedefinablePoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean canEqual(Object obj) {
			return obj instanceof SubclassForAbstractRedefinablePoint;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassForAbstractRedefinablePoint)) {
				return false;
			}
			SubclassForAbstractRedefinablePoint p = (SubclassForAbstractRedefinablePoint)obj;
			return p.canEqual(this) && super.equals(obj) && color == p.color;
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
}
