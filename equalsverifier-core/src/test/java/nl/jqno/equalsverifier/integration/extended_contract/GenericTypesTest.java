package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class GenericTypesTest {

    @Test
    void succeed_whenClassHasGenericFieldThatsSpecifiedToABuiltinGeneric() {
        EqualsVerifier.forClass(GenericContainerWithBuiltin.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtJava8TypesGenericContent() {
        EqualsVerifier.forClass(JavaGenericTypeContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtListFieldsGenericContent() {
        EqualsVerifier.forClass(ListContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtSetFieldsGenericContent() {
        EqualsVerifier.forClass(SetContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtMapFieldsGenericContent() {
        EqualsVerifier.forClass(MapContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtListOfTFieldsGenericContent() {
        EqualsVerifier.forClass(ListOfTContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtArrayOfTFieldsGenericContent() {
        EqualsVerifier.forClass(ArrayOfTContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayEqualsContainer.class).verify();
    }

    @Test
    void succeed_whenHashCodeLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayHashCodeContainer.class).verify();
    }

    @Test
    void succeed_whenToStringLooksAtNonCollectionGenericContent() {
        EqualsVerifier.forClass(SparseArrayToStringContainer.class).verify();
    }

    @Test
    void succeed_whenEqualsLooksAtGenericContent_givenTwoGenericFields() {
        EqualsVerifier.forClass(TwoGenericsContainerWithIntrospection.class).verify();
    }

    @Test
    void succeed_whenClassHasTypeVariableThatExtendsSomething() {
        EqualsVerifier.forClass(TypeVariableExtendsContainer.class).verify();
    }

    @Test
    void succeed_whenClassHasTypeVariableThatExtendsSomethingThatSupersSomething() {
        EqualsVerifier.forClass(TypeVariableExtendsWithSuperContainer.class).verify();
    }

    @Test
    void failGracefully_whenClassHasASelfReferenceGenericParameter() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SelfReferringGenericType.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Reflection error",
                    "try adding a prefab value",
                    "field wrapped",
                    "of type " + SelfReferringGenericType.class.getName());
    }

    @Test
    void succeed_whenClassHasASelfReferenceGenericParameter_givenPrefabValues() {
        EqualsVerifier
                .forClass(SelfReferringGenericType.class)
                .withPrefabValues(
                    SelfReferringGenericType.class,
                    new SelfReferringGenericType<>(1),
                    new SelfReferringGenericType<>(2))
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void succeed_whenClassContainsAMapWithAnArray() {
        EqualsVerifier.forClass(ArrayMapContainer.class).withNonnullFields("map").verify();
    }

    @Test
    void succeed_whenClassContainsAClassThatContainsAMapWithArray() {
        EqualsVerifier.forClass(ArrayMapContainerContainer.class).withNonnullFields("mapContainer").verify();
    }

    static final class GenericContainerWithBuiltin {

        private final Generic<List<String>> b;

        public GenericContainerWithBuiltin(Generic<List<String>> b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GenericContainerWithBuiltin)) {
                return false;
            }
            GenericContainerWithBuiltin other = (GenericContainerWithBuiltin) obj;
            return Objects.equals(b, other.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(b);
        }
    }

    static final class Generic<T> {

        private final T t;

        public Generic(T t) {
            this.t = t;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Generic)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Generic<T> other = (Generic<T>) obj;
            return Objects.equals(t, other.t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(t);
        }
    }

    static final class JavaGenericTypeContainer {

        private final Optional<Point> optional;
        private final Supplier<Point> supplier;
        private final AtomicReferenceArray<Point> atomicReferenceArray;

        public JavaGenericTypeContainer(
                Optional<Point> optional,
                Supplier<Point> supplier,
                AtomicReferenceArray<Point> atomicReferenceArray) {
            this.optional = optional;
            this.supplier = supplier;
            this.atomicReferenceArray = atomicReferenceArray;
        }

        // CHECKSTYLE OFF: NPathComplexity
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaGenericTypeContainer)) {
                return false;
            }
            JavaGenericTypeContainer other = (JavaGenericTypeContainer) obj;
            Point thisOptionalPoint = optional != null ? optional.orElse(null) : null;
            Point thatOptionalPoint = other.optional != null ? other.optional.orElse(null) : null;
            Point thisSupplierPoint = supplier != null ? supplier.get() : null;
            Point thatSupplierPoint = other.supplier != null ? other.supplier.get() : null;
            Point thisAraPoint = atomicReferenceArray != null ? atomicReferenceArray.get(0) : null;
            Point thatAraPoint = other.atomicReferenceArray != null ? other.atomicReferenceArray.get(0) : null;
            return Objects.equals(thisOptionalPoint, thatOptionalPoint)
                    && Objects.equals(thisSupplierPoint, thatSupplierPoint)
                    && Objects.equals(thisAraPoint, thatAraPoint);
        }

        // CHECKSTYLE ON: NPathComplexity

        @Override
        public int hashCode() {
            return Objects.hash(optional, supplier, atomicReferenceArray);
        }

        @Override
        public String toString() {
            return "JavaGenericTypeContainer: " + optional + ", " + supplier.get();
        }
    }

    static final class ListContainer {

        private final List<Point> list;

        public ListContainer(List<Point> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListContainer)) {
                return false;
            }
            ListContainer other = (ListContainer) obj;
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

        @Override
        public int hashCode() {
            return Objects.hash(list);
        }

        @Override
        public String toString() {
            return "ListContainer: " + list;
        }
    }

    static final class SetContainer {

        private final Set<Point> set;

        public SetContainer(Set<Point> set) {
            this.set = set;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SetContainer)) {
                return false;
            }
            SetContainer other = (SetContainer) obj;
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

        @Override
        public int hashCode() {
            return Objects.hash(set);
        }

        @Override
        public String toString() {
            return "SetContainer: " + set;
        }
    }

    static final class MapContainer {

        private final Map<Point, Point> map;

        public MapContainer(Map<Point, Point> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MapContainer)) {
                return false;
            }
            MapContainer other = (MapContainer) obj;
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

        @Override
        public int hashCode() {
            return Objects.hash(map);
        }

        @Override
        public String toString() {
            return "MapContainer: " + map;
        }
    }

    static final class ListOfTContainer<T> {

        private final ArrayList<T> list;

        public ListOfTContainer(ArrayList<T> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListOfTContainer)) {
                return false;
            }
            ListOfTContainer<?> other = (ListOfTContainer<?>) obj;
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

        @Override
        public int hashCode() {
            return Objects.hash(list);
        }

        @Override
        public String toString() {
            return "ListOfTContainer: " + list;
        }
    }

    static final class ArrayOfTContainer<T> {

        private final T[] array;

        public ArrayOfTContainer(T[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayOfTContainer)) {
                return false;
            }
            ArrayOfTContainer<?> other = (ArrayOfTContainer<?>) obj;
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

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }

        @Override
        public String toString() {
            return "ArrayOfTContainer: " + array;
        }
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
            SparseArrayEqualsContainer other = (SparseArrayEqualsContainer) obj;
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
            return obj instanceof SparseArrayHashCodeContainer other && Objects.equals(sparseArray, other.sparseArray);
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

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SparseArrayToStringContainer other && Objects.equals(sparseArray, other.sparseArray);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sparseArray);
        }

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

    public static final class TwoGenericsContainerWithIntrospection {

        private final List<String> stringList = List.of();
        private final List<Integer> intList = List.of();

        @SuppressWarnings("unused")
        @Override
        public boolean equals(Object obj) {
            if (stringList != null && stringList.size() > 0) {
                String key = stringList.get(0); // force a cast
            }
            if (intList != null && intList.size() > 0) {
                Integer key = intList.get(0); // force a cast
            }

            if (!(obj instanceof TwoGenericsContainerWithIntrospection)) {
                return false;
            }
            TwoGenericsContainerWithIntrospection other = (TwoGenericsContainerWithIntrospection) obj;
            return Objects.equals(stringList, other.stringList) && Objects.equals(intList, other.intList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringList, intList);
        }
    }

    static final class TypeVariableExtendsContainer<I extends Comparable<I>> {

        private final I id;

        protected TypeVariableExtendsContainer(I id) {
            this.id = id;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            return obj instanceof TypeVariableExtendsContainer other && Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    static final class TypeVariableExtendsWithSuperContainer<I extends Comparable<? super I>> {

        private final I id;

        protected TypeVariableExtendsWithSuperContainer(I id) {
            this.id = id;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            return obj instanceof TypeVariableExtendsWithSuperContainer other && Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class SelfReferringGenericType<T extends SelfReferringGenericType<T>> {

        private T wrapped;

        // Everything below is boilerplate to be able to run the tests; the bit above this comment is what matters

        private final int i;

        public SelfReferringGenericType(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof SelfReferringGenericType)) {
                return false;
            }
            SelfReferringGenericType<?> other = (SelfReferringGenericType<?>) obj;
            return i == other.i && Objects.equals(wrapped, other.wrapped);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i, wrapped);
        }
    }

    static class ArrayMapContainerContainer {

        private final ArrayMapContainer mapContainer;

        public ArrayMapContainerContainer(final ArrayMapContainer mapContainer) {
            this.mapContainer = mapContainer;
        }

        @Override
        public final boolean equals(final Object o) {
            if (!(o instanceof ArrayMapContainerContainer)) {
                return false;
            }
            final ArrayMapContainerContainer that = (ArrayMapContainerContainer) o;
            return Objects.equals(mapContainer, that.mapContainer);
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(mapContainer);
        }
    }

    static class ArrayMapContainer {

        private final Map<String, byte[]> map;

        public ArrayMapContainer(final Map<String, byte[]> map) {
            this.map = map;
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ArrayMapContainer)) {
                return false;
            }
            final ArrayMapContainer that = (ArrayMapContainer) o;
            if (map.size() != that.map.size()) {
                return false;
            }
            for (final Map.Entry<String, byte[]> entry : map.entrySet()) {
                if (!Arrays.equals(entry.getValue(), that.map.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final int hashCode() {
            int hashCode = 0;
            for (final Map.Entry<String, byte[]> entry : map.entrySet()) {
                hashCode += entry.getKey().hashCode() ^ Arrays.hashCode(entry.getValue());
            }
            return hashCode;
        }
    }
}
