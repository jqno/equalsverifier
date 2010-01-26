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
package nl.jqno.equalsverifier.points;


public final class BlindlyEqualsColorPoint extends BlindlyEqualsPoint {
	private final Color color;

	public BlindlyEqualsColorPoint(int x, int y, Color color) {
		super(x, y);
		this.color = color;
	}

	@Override
    protected boolean blindlyEquals(Object o) {
        if (!(o instanceof BlindlyEqualsColorPoint)) {
        	return false;
        }
        BlindlyEqualsColorPoint cp = (BlindlyEqualsColorPoint)o;
        return (super.blindlyEquals(cp) && 
        		cp.color == this.color);
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
