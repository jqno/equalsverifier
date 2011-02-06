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

public class HashCodeTest {
	@Test
	public void invalidHashCode() {
		EqualsVerifier<HashCodeBrokenPoint> ev = EqualsVerifier.forClass(HashCodeBrokenPoint.class);
		assertFailure(ev, "hashCode: hashCodes should be equal", HashCodeBrokenPoint.class.getSimpleName());
	}
	
	static class HashCodeBrokenPoint extends Point {
		public HashCodeBrokenPoint(int x, int y) {
			super(x, y);
		}

		@Override
		public int hashCode() {
			return new Object().hashCode();
		}
	}
}
