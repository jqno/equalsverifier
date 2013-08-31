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

import nl.jqno.equalsverifier.testhelpers.points.Color;

public final class NetBeansGetClassPoint {
    private final int x;
    private final int y;
    private final Color color;

    public NetBeansGetClassPoint(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.x;
        hash = 83 * hash + this.y;
        hash = 83 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NetBeansGetClassPoint other = (NetBeansGetClassPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.color != other.color) {
            return false;
        }
        return true;
    }
}

