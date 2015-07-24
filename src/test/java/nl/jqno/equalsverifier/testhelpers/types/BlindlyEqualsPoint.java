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
package nl.jqno.equalsverifier.testhelpers.types;

public class BlindlyEqualsPoint {
    private final int x;
    private final int y;

    public BlindlyEqualsPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected boolean blindlyEquals(Object o) {
        if (!(o instanceof BlindlyEqualsPoint)) {
            return false;
        }
        BlindlyEqualsPoint p = (BlindlyEqualsPoint)o;
        return p.x == this.x && p.y == this.y;
    }

    @Override
    public boolean equals(Object o) {
        return (this.blindlyEquals(o) && ((BlindlyEqualsPoint)o).blindlyEquals(this));
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
