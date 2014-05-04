/*
 * Copyright 2009-2011, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.inheritance;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.points.BlindlyEqualsColorPoint;
import nl.jqno.equalsverifier.testhelpers.points.BlindlyEqualsPoint;
import nl.jqno.equalsverifier.testhelpers.points.CanEqualColorPoint;
import nl.jqno.equalsverifier.testhelpers.points.CanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.points.Color;
import nl.jqno.equalsverifier.testhelpers.points.EqualSubclassForBlindlyEqualsPoint;
import nl.jqno.equalsverifier.testhelpers.points.EqualSubclassForCanEqualPoint;

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
public class SubclassTest {
	@Test
	public void fail_whenEqualsIsOverridableAndBlindlyEqualsIsPresent() {
		EqualsVerifier<BlindlyEqualsPoint> ev = EqualsVerifier.forClass(BlindlyEqualsPoint.class)
				.withRedefinedSubclass(EqualSubclassForBlindlyEqualsPoint.class);
		assertFailure(ev, "Subclass", BlindlyEqualsPoint.class.getSimpleName(), "equals subclass instance", EqualSubclassForBlindlyEqualsPoint.class.getSimpleName());
	}
	
	@Test
	public void succeed_whenEqualsIsOverridableAndBlindlyEqualsIsPresent_givenACorrectSubclass() {
		EqualsVerifier.forClass(BlindlyEqualsPoint.class)
				.withRedefinedSubclass(BlindlyEqualsColorPoint.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsIsOverriddenTwiceThroughBlindlyEquals_givenWithRedefinedSuperclass() {
		EqualsVerifier.forClass(BlindlyEqualsColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
	@Test
	public void fail_whenEqualsIsOverridableAndCanEqualIsPresent() {
		EqualsVerifier<CanEqualPoint> ev = EqualsVerifier.forClass(CanEqualPoint.class)
				.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class);
		assertFailure(ev, "Subclass", CanEqualPoint.class.getSimpleName(), "equals subclass instance", EqualSubclassForCanEqualPoint.class.getSimpleName());
	}
	
	@Test
	public void succeed_whenEqualsIsOverridableAndCanEqualIsPresent_givenACorrectSubclass() {
		EqualsVerifier.forClass(CanEqualPoint.class)
				.withRedefinedSubclass(CanEqualColorPoint.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsIsOverridenTwiceThroughCanEqual_givenWithRedefinedSuperclass() {
		EqualsVerifier.forClass(CanEqualColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
	@Test
	public void fail_whenWithRedefinedEqualsIsUsed_givenEqualsAndHashCodeAreFinal() {
		EqualsVerifier<FinalEqualsAndHashCode> ev = EqualsVerifier.forClass(FinalEqualsAndHashCode.class)
				.withRedefinedSubclass(RedeFinalSubPoint.class);
		assertFailure(ev, "Subclass", FinalEqualsAndHashCode.class.getSimpleName(),
				"has a final equals method", "No need to supply a redefined subclass");
	}
	
	@Test
	public void succeed_whenClassIsAbstract_givenACorrectImplementationOfEqualsUnderInheritanceAndARedefinedSubclass() {
		EqualsVerifier.forClass(AbstractRedefinablePoint.class)
				.withRedefinedSubclass(SubclassForAbstractRedefinablePoint.class)
				.verify();
	}
	
	@Test
	public void fail_whenWithRedefinedSubclassIsUsed_givenStrictInheritanceWarningIsSuppressed() {
		EqualsVerifier<CanEqualPoint> ev = EqualsVerifier.forClass(CanEqualPoint.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class);
		assertFailure(ev, "withRedefinedSubclass", "weakInheritanceCheck", "are mutually exclusive");
	}
	
	@Test
	public void fail_whenStrictInhertianceWarningIsSuppressed_givenWithRedefinedSubclassIsUsed() {
		EqualsVerifier<CanEqualPoint> ev = EqualsVerifier.forClass(CanEqualPoint.class)
				.withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
				.suppress(Warning.STRICT_INHERITANCE);
		assertFailure(ev, "withRedefinedSubclass", "weakInheritanceCheck", "are mutually exclusive");
	}
	
	static class FinalEqualsAndHashCode {
		private final int x;
		private final int y;
		
		public FinalEqualsAndHashCode(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof FinalEqualsAndHashCode)) {
				return false;
			}
			FinalEqualsAndHashCode p = (FinalEqualsAndHashCode)obj;
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
	
	static final class RedeFinalSubPoint extends FinalEqualsAndHashCode {
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
		
		public SubclassForAbstractRedefinablePoint(int x, int y, Color color) {
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
			return nullSafeHashCode(color) + (31 * super.hashCode());
		}
		
		@Override
		public String toString() {
			return super.toString() + "," + color;
		}
	}
}
