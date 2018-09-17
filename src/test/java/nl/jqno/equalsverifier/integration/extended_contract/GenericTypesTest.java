package nl.jqno.equalsverifier.integration.extended_contract;

import com.google.common.collect.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class GenericTypesTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenEqualsLooksAtJava8TypesGenericContent() {
        EqualsVerifier.forClass(JavaGenericTypeContainer.class)
                .verify();
    }

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

    @Test
    public void succeed_whenEqualsLooksAtImmutableCollectionFieldsGenericContent() {
        EqualsVerifier.forClass(ImmutableCollectionContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtImmutableListFieldsGenericContent() {
        EqualsVerifier.forClass(ImmutableListContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtImmutableMapFieldsGenericContent() {
        EqualsVerifier.forClass(ImmutableMapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtBiMapFieldsGenericContent() {
        EqualsVerifier.forClass(BiMapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtRangeEndpointGenericContent() {
        EqualsVerifier.forClass(RangeContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayEqualsContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenHashCodeLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayHashCodeContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenToStringLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayToStringContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasTypeVariableThatExtendsSomething() {
        EqualsVerifier.forClass(TypeVariableExtendsContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasTypeVariableThatExtendsSomethingThatSupersSomething() {
        EqualsVerifier.forClass(TypeVariableExtendsWithSuperContainer.class)
                .verify();
    }

    static final class JavaGenericTypeContainer {
        private final Optional<Point> optional;
        private final Supplier<Point> supplier;
        private final AtomicReferenceArray<Point> atomicReferenceArray;

        public JavaGenericTypeContainer(Optional<Point> optional, Supplier<Point> supplier, AtomicReferenceArray<Point> atomicReferenceArray) {
            this.optional = optional; this.supplier = supplier; this.atomicReferenceArray = atomicReferenceArray;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaGenericTypeContainer)) {
                return false;
            }
            JavaGenericTypeContainer other = (JavaGenericTypeContainer)obj;
            Point thisOptionalPoint = optional != null ? optional.orElse(null) : null;
            Point thatOptionalPoint = other.optional != null ? other.optional.orElse(null) : null;
            Point thisSupplierPoint = supplier != null ? supplier.get() : null;
            Point thatSupplierPoint = other.supplier != null ? other.supplier.get() : null;
            Point thisAraPoint = atomicReferenceArray != null ? atomicReferenceArray.get(0) : null;
            Point thatAraPoint = other.atomicReferenceArray != null ? other.atomicReferenceArray.get(0) : null;
            return Objects.equals(thisOptionalPoint, thatOptionalPoint) && Objects.equals(thisSupplierPoint, thatSupplierPoint) && Objects.equals(thisAraPoint, thatAraPoint);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "JavaGenericTypeContainer: " + optional + ", " + supplier.get(); }
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
        private final ArrayList<T> list;

        public ListOfTContainer(ArrayList<T> list) { this.list = list; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListOfTContainer)) {
                return false;
            }
            ListOfTContainer<?> other = (ListOfTContainer<?>)obj;
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
            ArrayOfTContainer<?> other = (ArrayOfTContainer<?>)obj;
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

    static final class ImmutableCollectionContainer {
        private final ImmutableCollection<Point> coll;

        public ImmutableCollectionContainer(ImmutableCollection<Point> coll) { this.coll = coll; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ImmutableCollectionContainer)) {
                return false;
            }
            ImmutableCollectionContainer other = (ImmutableCollectionContainer)obj;
            if (coll == null || other.coll == null) {
                return coll == other.coll;
            }
            if (coll.size() != other.coll.size()) {
                return false;
            }
            for (Point p : coll) {
                if (!other.coll.contains(p)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "ImmutableCollectionContainer: " + coll; }
    }

    static final class ImmutableListContainer {
        private final ImmutableList<Point> list;

        public ImmutableListContainer(ImmutableList<Point> list) { this.list = list; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ImmutableListContainer)) {
                return false;
            }
            ImmutableListContainer other = (ImmutableListContainer)obj;
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
        @Override public String toString() { return "ImmutableListContainer: " + list; }
    }

    static final class ImmutableMapContainer {
        private final ImmutableMap<Point, Point> map;

        public ImmutableMapContainer(ImmutableMap<Point, Point> map) { this.map = map; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ImmutableMapContainer)) {
                return false;
            }
            ImmutableMapContainer other = (ImmutableMapContainer)obj;
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
        @Override public String toString() { return "ImmutableMapContainer: " + map; }
    }

    static final class RangeContainer {
        private final Range<String> range;

        public RangeContainer(Range<String> coll) { this.range = coll; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RangeContainer)) {
                return false;
            }
            RangeContainer other = (RangeContainer)obj;
            if (range == null || other.range == null) {
                return range == other.range;
            }
            String lower = range.lowerEndpoint();
            String otherLower = other.range.lowerEndpoint();
            if (!lower.equals(otherLower)) {
                return false;
            }
            return range.equals(other.range);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "ImmutableCollectionContainer: " + range; }
    }

    static final class BiMapContainer {
        private final BiMap<Point, Point> map;

        public BiMapContainer(BiMap<Point, Point> map) { this.map = map; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BiMapContainer)) {
                return false;
            }
            BiMapContainer other = (BiMapContainer)obj;
            if (map == null || other.map == null) {
                return map == other.map;
            }
            if (map.size() != other.map.size()) {
                return false;
            }
            for (Map.Entry<Point, Point> e : map.entrySet()) {
                Point thisKey = e.getKey();
                Point thisValue = e.getValue();
                if (!other.map.containsKey(thisKey)) {
                    return false;
                }
                if (!other.map.get(thisKey).equals(thisValue)) {
                    return false;
                }
            }
            return true;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
        @Override public String toString() { return "BiMapContainer: " + map; }
    }

    static final class SparseArray<T> {
        private final List<T> items;

        public SparseArray(List<T> items) {
            this.items = items;
        }

        public int size() {
            return items.size();
        }

        public T get(int i) {
            return items.get(i);
        }

        // There are no equals and hashCode
    }

    static final class SparseArrayEqualsContainer {
        private final SparseArray<Point> sparseArray;

        public SparseArrayEqualsContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SparseArrayEqualsContainer)) {
                return false;
            }
            SparseArrayEqualsContainer other = (SparseArrayEqualsContainer)obj;
            if (sparseArray == null || other.sparseArray == null) {
                return sparseArray == other.sparseArray;
            }
            if (sparseArray.size() != other.sparseArray.size()) {
                return false;
            }
            for (int i = 0; i < sparseArray.size(); i++) {
                Point a = sparseArray.get(i);
                Point b = other.sparseArray.get(i);
                if (!a.equals(b)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 17;
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += 59 * sparseArray.get(i).hashCode();
                }
            }
            return result;
        }
    }

    static final class SparseArrayHashCodeContainer {
        private final SparseArray<Point> sparseArray;

        public SparseArrayHashCodeContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            int result = 17;
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += 59 * sparseArray.get(i).hashCode();
                }
            }
            return result;
        }
    }

    static final class SparseArrayToStringContainer {
        private final SparseArray<Point> sparseArray;

        public SparseArrayToStringContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }

        @Override
        public String toString() {
            String result = SparseArrayToStringContainer.class.getSimpleName() + ": ";
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += sparseArray.get(i) + ", ";
                }
            }
            return result;
        }
    }

    @SuppressWarnings("unused")
    static final class TypeVariableExtendsContainer<I extends Comparable<I>> {
        private final I id;

        protected TypeVariableExtendsContainer(I id) { this.id = id; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @SuppressWarnings("unused")
    static final class TypeVariableExtendsWithSuperContainer<I extends Comparable<? super I>> {
        private final I id;

        protected TypeVariableExtendsWithSuperContainer(I id) { this.id = id; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
