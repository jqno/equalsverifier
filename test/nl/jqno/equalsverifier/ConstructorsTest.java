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

import static nl.jqno.equalsverifier.Helper.assertFailure;
import nl.jqno.equalsverifier.points.CanEqualColorPoint;
import nl.jqno.equalsverifier.points.CanEqualPoint;
import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.FinalPoint;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class ConstructorsTest {
	@Test
	public void classSuccess() {
		EqualsVerifier.forClass(FinalPoint.class).verify();
	}
	
	@Test
	public void examplesSuccess() {
		FinalPoint red = new FinalPoint(1, 2);
		FinalPoint black = new FinalPoint(2, 3);
		FinalPoint green = new FinalPoint(3, 2);
		FinalPoint blue = new FinalPoint(2, 1);
		
		EqualsVerifier.forExamples(red, black, green, blue).verify();
	}
	
	@Test
	public void examplesFail() {
		Point red = new Point(1, 2);
		Point black = new Point(2, 3);
		EqualsVerifier<Point> ev = EqualsVerifier.forExamples(red, black);
		assertFailure(ev, "Subclass:");
	}
	
	@Test
	public void examplesContainsSubclasses() {
		CanEqualPoint red = new CanEqualPoint(1, 2);
		CanEqualPoint black = new CanEqualPoint(2, 3);
		CanEqualColorPoint green = new CanEqualColorPoint(1, 2, Color.INDIGO);
		
		EqualsVerifier.forExamples(red, black, green)
				.withRedefinedSubclass(CanEqualColorPoint.class)
				.verify();
	}
	
	@Test
	public void classWithPrefabExamples() {
		EqualsVerifier.forClass(FinalPoint.class)
				.withPrefabValues(FinalPoint.class, new FinalPoint(1, 2), new FinalPoint(2, 3))
				.verify();
	}
}
