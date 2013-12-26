/*
 * Copyright 2009-2010, 2013 Jan Ouwens
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
import nl.jqno.equalsverifier.testhelpers.points.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PreconditionTest {
	private static final String PRECONDITION = "Precondition";
	
	FinalPoint first;
	FinalPoint second;
	
	@Before
	public void setup() {
		first = new FinalPoint(1, 2);
		second = new FinalPoint(2, 3);
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void firstNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("First example is null.");
		
		EqualsVerifier.forExamples(null, second);
	}
	
	@Test
	public void secondNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Second example is null.");
		
		EqualsVerifier.forExamples(first, null);
	}
	
	@Test
	public void moreNull() {
		EqualsVerifier.forExamples(first, second, (FinalPoint[])null).verify();
	}
	
	@Test
	public void anyNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("One of the examples is null.");
		
		FinalPoint another = new FinalPoint(3, 4);
		EqualsVerifier.forExamples(first, second, another, null);
	}
	
	@Test
	public void incompatibleClass() {
		EqualsVerifier<?> ev = EqualsVerifier.forExamples(first, new Point(1, 2));
		assertFailure(ev, PRECONDITION, "are of different classes", FinalPoint.class.getSimpleName(), Point.class.getSimpleName());
	}
	
	@Test
	public void allSame() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, first);
		assertFailure(ev, PRECONDITION, "the same object appears twice", FinalPoint.class.getSimpleName());
	}
	
	@Test
	public void allEqual() {
		FinalPoint firstest = new FinalPoint(1, 2);
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, firstest);
		assertFailure(ev, PRECONDITION, "two objects are equal to each other", FinalPoint.class.getSimpleName());
	}
}
