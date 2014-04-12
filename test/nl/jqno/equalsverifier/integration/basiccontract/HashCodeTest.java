/*
 * Copyright 2009-2010, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.basiccontract;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Test;

public class HashCodeTest {
	@Test
	public void fail_whenHashCodesAreUnequal_givenEqualObjects() {
		EqualsVerifier<RandomHashCode> ev = EqualsVerifier.forClass(RandomHashCode.class);
		assertFailure(ev, "hashCode: hashCodes should be equal", RandomHashCode.class.getSimpleName());
	}
	
	static class RandomHashCode extends Point {
		public RandomHashCode(int x, int y) {
			super(x, y);
		}

		@Override
		public int hashCode() {
			return new Object().hashCode();
		}
	}
}
