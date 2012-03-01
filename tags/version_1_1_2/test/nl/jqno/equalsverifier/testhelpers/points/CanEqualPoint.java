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
package nl.jqno.equalsverifier.testhelpers.points;

public class CanEqualPoint {
	private final int x;
	private final int y;

	public CanEqualPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean canEqual(Object obj) {
		return obj instanceof CanEqualPoint;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CanEqualPoint)) {
			return false;
		}
		CanEqualPoint p = (CanEqualPoint)obj;
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
