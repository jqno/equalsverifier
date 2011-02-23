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

public class UnredefinableSubclassTest {
	private static final String SUBCLASS = "Subclass";
	private static final String SUPPLY_AN_INSTANCE = "Supply an instance of a redefined subclass using withRedefinedSubclass";

	@Test
	public void equalsFinal() {
		EqualsVerifier<Point> ev = EqualsVerifier.forClass(Point.class);
		assertFailure(ev, SUBCLASS, "equals is not final", SUPPLY_AN_INSTANCE, "if equals cannot be final");
	}
	
	@Test
	public void hashCodeFinal() {
		EqualsVerifier<FinalEqualsPoint> ev = EqualsVerifier.forClass(FinalEqualsPoint.class);
		assertFailure(ev, SUBCLASS, "hashCode is not final", SUPPLY_AN_INSTANCE, "if hashCode cannot be final");
	}
	
	@Test
	public void notFinal() {
		EqualsVerifier.forClass(Point.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
		
		EqualsVerifier.forClass(FinalEqualsPoint.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	static class FinalEqualsPoint extends Point {
		public FinalEqualsPoint(int x, int y) {
			super(x, y);
		}

		@Override
		public final boolean equals(Object obj) {
			return super.equals(obj);
		}
	}
}
