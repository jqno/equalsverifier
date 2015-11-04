/*
 * Copyright 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class GenericTypesTest {
    @Test
    public void succeed_whenEqualsLooksAtListFieldsGenericContent() {
        EqualsVerifier.forClass(ListContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtSetFieldsGenericContent() {
        EqualsVerifier.forClass(SetContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtMapFieldsGenericContent() {
        EqualsVerifier.forClass(MapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtListOfTFieldsGenericContent() {
        EqualsVerifier.forClass(ListOfTContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtArrayOfTFieldsGenericContent() {
        EqualsVerifier.forClass(ArrayOfTContainer.class)
                .verify();
    }

    static final class ListContainer {
        private final List<Point> list;

        public ListContainer(List<Point> list) { this.list = list; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListContainer)) {
                return false;
            }
            ListContainer other = (ListContainer)obj;
            if (list == null || other.list == null) {
                return list == other.list;
            }
            if (list.size() != other.list.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                Point x = list.get(i);
                Point y = other.list.get(i);
                if (!x.equals(y)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "ListContainer: " + list; }
    }

    static final class SetContainer {
        private final Set<Point> set;

        public SetContainer(Set<Point> set) { this.set = set; }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SetContainer)) {
                return false;
            }
            SetContainer other = (SetContainer)obj;
            if (set == null || other.set == null) {
                return set == other.set;
            }
            if (set.size() != other.set.size()) {
                return false;
            }
            for (Point p : set) {
                if (!other.set.contains(p)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "SetContainer: " + set; }
    }

    static final class MapContainer {
        private final Map<Point, Point> map;

        public MapContainer(Map<Point, Point> map) { this.map = map; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MapContainer)) {
                return false;
            }
            MapContainer other = (MapContainer)obj;
            if (map == null || other.map == null) {
                return map == other.map;
            }
            if (map.size() != other.map.size()) {
                return false;
            }
            for (Map.Entry<Point, Point> e : map.entrySet()) {
                if (!other.map.containsKey(e.getKey())) {
                    return false;
                }
                if (!other.map.get(e.getKey()).equals(e.getValue())) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "MapContainer: " + map; }
    }

    static final class ListOfTContainer<T> {
        private final List<T> list;

        public ListOfTContainer(List<T> list) { this.list = list; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListOfTContainer)) {
                return false;
            }
            ListOfTContainer other = (ListOfTContainer)obj;
            if (list == null || other.list == null) {
                return list == other.list;
            }
            if (list.size() != other.list.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                T x = list.get(i);
                Object y = other.list.get(i);
                if (!x.equals(y)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "ListOfTContainer: " + list; }
    }

    static final class ArrayOfTContainer<T> {
        private final T[] array;

        public ArrayOfTContainer(T[] array) { this.array = array; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayOfTContainer)) {
                return false;
            }
            ArrayOfTContainer other = (ArrayOfTContainer)obj;
            if (array == null || other.array == null) {
                return array == other.array;
            }
            if (array.length != other.array.length) {
                return false;
            }
            for (int i = 0; i < array.length; i++) {
                T x = array[i];
                Object y = other.array[i];
                if (!x.equals(y)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return Arrays.hashCode(array); }
        @Override public String toString() { return "ArrayOfTContainer: " + array; }
    }
}
