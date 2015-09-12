/*
 * Copyright 2013 Jan Ouwens
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
package nl.jqno.equalsverifier.coverage;

import nl.jqno.equalsverifier.testhelpers.types.Color;

public final class HandwrittenGetClassPoint {
    private final int x;
    private final int y;
    private final Color color;

    public HandwrittenGetClassPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HandwrittenGetClassPoint other = (HandwrittenGetClassPoint)obj;
        return x == other.x && y == other.y && (color == null ? other.color == null : color.equals(other.color));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (color == null ? 0 : color.hashCode());
        return result;
    }
}
