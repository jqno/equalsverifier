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

import nl.jqno.equalsverifier.points.Color;

public final class RedefinableColorPoint extends RedefinablePoint {
	private final Color color;

	public RedefinableColorPoint(int x, int y, Color color) {
		super(x, y);
		this.color = color;
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof RedefinableColorPoint;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RedefinableColorPoint)) {
			return false;
		}
		RedefinableColorPoint p = (RedefinableColorPoint)obj;
		return p.canEqual(this) && super.equals(p) && color == p.color;
	}
	
	@Override
	public int hashCode() {
		return (color == null ? 0 : color.hashCode()) + (31 * super.hashCode());
	}
	
	@Override
	public String toString() {
		return super.toString() + "," + color;
	}
}
