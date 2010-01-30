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
import nl.jqno.equalsverifier.points.BlindlyEqualsColorPoint;
import nl.jqno.equalsverifier.points.BlindlyEqualsPoint;
import nl.jqno.equalsverifier.points.CanEqualColorPoint;
import nl.jqno.equalsverifier.points.CanEqualPoint;
import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.ColorBlindColorPoint;
import nl.jqno.equalsverifier.points.EqualSubclassForBlindlyEqualsPoint;
import nl.jqno.equalsverifier.points.EqualSubclassForCanEqualPoint;

import org.junit.Test;

/**
 * Tests, among other things, the following approaches to inheritance with added
 * fields:
 * 
 * 1. "blindly equals", as described by Tal Cohen in Dr. Dobb's Journal, May
 *    2002. See also http://www.ddj.com/java/184405053 and
 *    http://tal.forum2.org/equals
 * 
 * 2. "can equal", as described by Odersky, Spoon and Venners in Programming in
 *    Scala.
 */
public class SubclassTest extends EqualsVerifierTestBase {
	@Test
	public void blindlyEqualsReferenceEqualsSub() {
		EqualsVerifier<BlindlyEqualsPoint> ev = EqualsVerifier.forClass(BlindlyEqualsPoint.class)
				.withRedefinedSubclass(EqualSubclassForBlindlyEqualsPoint.class);
		verifyFailure("Subclass:\n  BlindlyEqualsPoint:1,1\nequals subclass instance\n  EqualSubclassForBlindlyEqualsPoint:1,1", ev);
	}
	
	@Test
	public void blindlyEqualsSanityEqualsIsValidForSuper() {
		EqualsVerifier.forClass(BlindlyEqualsPoint.class)
				.withRedefinedSubclass(BlindlyEqualsColorPoint.class)
				.verify();
	}
	
	@Test
	public void blindlyEqualsWithRedefinedSuperclass() {
		EqualsVerifier.forClass(BlindlyEqualsColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
	@Test
	public void canEqualReferenceEqualsSub() {
		EqualsVerifier<CanEqualPoint> ev = EqualsVerifier.forClass(CanEqualPoint.class)
				.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class);
		verifyFailure("Subclass:\n  CanEqualPoint:1,1\nequals subclass instance\n  EqualSubclassForCanEqualPoint:1,1", ev);
	}
	
	@Test
	public void canEqualSanityEqualsIsValidForSuper() {
		EqualsVerifier.forClass(CanEqualPoint.class)
				.withRedefinedSubclass(CanEqualColorPoint.class)
				.verify();
	}
	
	@Test
	public void canEqualWithRedefinedSuperclass() {
		EqualsVerifier.forClass(CanEqualColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
	@Test
	public void invalidWithRedefinedSuperclass() {
		EqualsVerifier<ColorBlindColorPoint> ev = EqualsVerifier.forClass(ColorBlindColorPoint.class);
		ev.withRedefinedSuperclass();
		verifyFailure("Redefined superclass:\n  ColorBlindColorPoint:1,1,YELLOW\nmay not equal superclass instance\n  Point:1,1\nbut it does.", ev);
	}
	
	@Test
	public void equalsMethodFinalSoNoRedefinedSubclassNecessary() {
		EqualsVerifier<RedeFinalPoint> ev = EqualsVerifier.forClass(RedeFinalPoint.class)
				.withRedefinedSubclass(RedeFinalSubPoint.class);
		verifyFailure("Subclass: RedeFinalPoint has a final equals method.\nNo need to supply a redefined subclass.", ev);
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
			EqualsVerifier.forClass(CanEqualPoint.class)
					.suppress(Warning.STRICT_INHERITANCE)
					.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
					.verify();
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
			EqualsVerifier.forClass(CanEqualPoint.class)
					.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
					.suppress(Warning.STRICT_INHERITANCE)
					.verify();
			fail("Assertion didn't fail");
		}
		catch (AssertionError e) {
			assertEquals("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.",
					e.getMessage());
		}
	}
	
	static class RedeFinalPoint {
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
	
	static final class RedeFinalSubPoint extends RedeFinalPoint {
		public RedeFinalSubPoint(int x, int y) {
			super(x, y);
		}
	}
	
	static abstract class AbstractRedefinablePoint {
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
	
	static final class SubclassForAbstractRedefinablePoint extends AbstractRedefinablePoint {
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
