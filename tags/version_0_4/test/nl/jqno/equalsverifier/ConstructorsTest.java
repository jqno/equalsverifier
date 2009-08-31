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
import nl.jqno.equalsverifier.points.FinalPoint;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.redefinablepoint.RedefinableColorPoint;
import nl.jqno.equalsverifier.redefinablepoint.RedefinablePoint;

import org.junit.Test;

public class ConstructorsTest extends EqualsVerifierTestBase {
	@Test
	public void klassSuccess() {
		EqualsVerifier.forClass(FinalPoint.class).verify();
	}
	
	@Test
	public void examplesSuccess() {
		FinalPoint first = new FinalPoint(1, 2);
		FinalPoint second = new FinalPoint(2, 3);
		FinalPoint third = new FinalPoint(3, 2);
		FinalPoint fourth = new FinalPoint(2, 1);
		
		EqualsVerifier.forExamples(first, second, third, fourth).verify();
	}
	
	@Test
	public void examplesFail() {
		Point first = new Point(1, 2);
		Point second = new Point(2, 3);
		EqualsVerifier<Point> ev = EqualsVerifier.forExamples(first, second);
		verifyFailure("Subclass:", ev);
	}
	
	@Test
	public void examplesContainsSubclasses() {
		RedefinablePoint first = new RedefinablePoint(1, 2);
		RedefinablePoint second = new RedefinablePoint(2, 3);
		RedefinableColorPoint third = new RedefinableColorPoint(1, 2, Color.INDIGO);
		
		EqualsVerifier.forExamples(first, second, third)
				.withRedefinedSubclass(RedefinableColorPoint.class)
				.verify();
	}
}
