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

import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class UnredefinableSubclassTest extends EqualsVerifierTestBase {
	@Test
	public void equalsFinal() {
		EqualsVerifier<Point> ev = EqualsVerifier.forClass(Point.class);
		verifyFailure("Subclass: equals is not final. Supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.", ev);
	}
	
	@Test
	public void hashCodeFinal() {
		EqualsVerifier<FinalEqualsPoint> ev = EqualsVerifier.forClass(FinalEqualsPoint.class);
		verifyFailure("Subclass: hashCode is not final. Supply an instance of a redefined subclass using withRedefinedSubclass if hashCode cannot be final.", ev);
	}
	
	@Test
	public void notFinal() {
		EqualsVerifier.forClass(Point.class)
				.weakInheritanceCheck()
				.verify();
		
		EqualsVerifier.forClass(FinalEqualsPoint.class)
				.weakInheritanceCheck()
				.verify();
	}
	
	private static class FinalEqualsPoint extends Point {
		public FinalEqualsPoint(int x, int y) {
			super(x, y);
		}

		@Override
		public final boolean equals(Object obj) {
			return super.equals(obj);
		}
	}
}
