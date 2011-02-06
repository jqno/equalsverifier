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
package nl.jqno.equalsverifier.testhelpers.points;

import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

public class PointContainer {
	private final Point point;
	
	public PointContainer(Point point) {
		this.point = point;
	}
	
	public Point getPoint() {
		return point;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointContainer)) {
			return false;
		}
		PointContainer other = (PointContainer)obj;
		return nullSafeEquals(point, other.point);
	}
	
	@Override
	public int hashCode() {
		return nullSafeHashCode(point);
	}
}
