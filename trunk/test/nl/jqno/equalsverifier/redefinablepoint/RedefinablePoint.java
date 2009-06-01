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
package nl.jqno.equalsverifier.redefinablepoint;

public class RedefinablePoint {
	private final int x;
	private final int y;

	public RedefinablePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean canEqual(Object obj) {
		return obj instanceof RedefinablePoint;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RedefinablePoint)) {
			return false;
		}
		RedefinablePoint p = (RedefinablePoint)obj;
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
