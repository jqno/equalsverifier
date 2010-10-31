/*
 * Copyright 2010 Jan Ouwens
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

import org.junit.Test;

public class GetClassTest {
	@Test
	public void happyPath() {
		EqualsVerifier.forClass(GetClassPointHappyPath.class)
				.usingGetClass()
				.verify();
	}
	
	@Test
	public void nullCheckForgotten() {
		EqualsVerifier<GetClassPointNull> ev =
				EqualsVerifier.forClass(GetClassPointNull.class).usingGetClass();
		assertFailure(ev, "Non-nullity: NullPointerException thrown");
	}
	
	static class GetClassPointHappyPath {
		private final int x;
		private final int y;
		
		GetClassPointHappyPath(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			GetClassPointHappyPath p = (GetClassPointHappyPath)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	static class GetClassPointNull {
		private final int x;
		private final int y;
		
		GetClassPointNull(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj.getClass() != getClass()) {
				return false;
			}
			GetClassPointNull p = (GetClassPointNull)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
}
