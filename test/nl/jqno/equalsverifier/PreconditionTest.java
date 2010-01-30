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

import nl.jqno.equalsverifier.points.FinalPoint;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Before;
import org.junit.Test;

public class PreconditionTest extends EqualsVerifierTestBase {
	FinalPoint first;
	FinalPoint second;
	
	@Before
	public void setup() {
		first = new FinalPoint(1, 2);
		second = new FinalPoint(2, 3);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void firstNull() {
		EqualsVerifier.forExamples(null, second);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void secondNull() {
		EqualsVerifier.forExamples(first, null);
	}
	
	@Test
	public void tailNull() {
		EqualsVerifier.forExamples(first, second, (FinalPoint[])null).verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void anyNull() {
		FinalPoint another = new FinalPoint(3, 4);
		EqualsVerifier.forExamples(first, second, another, null);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void incompatibleClass() {
		EqualsVerifier ev = EqualsVerifier.forExamples(first, new Point(1, 2));
		verifyFailure("Precondition:\n  FinalPoint:1,2\nand\n  Point:1,2\nare of different classes", ev);
	}
	
	@Test
	public void allSame() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, first);
		verifyFailure("Precondition: the same object appears twice:\n  FinalPoint:1,2", ev);
	}
	
	@Test
	public void allEqual() {
		FinalPoint firstest = new FinalPoint(1, 2);
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, firstest);
		verifyFailure("Precondition: two objects are equal to each other:\n  FinalPoint:1,2", ev);
	}
}
