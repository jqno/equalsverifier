package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Arrays;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class ArrayTest {

    private static final String REGULAR_EQUALS =
        "Array: == or regular equals() used instead of Arrays.equals() for field";
    private static final String REGULAR_HASHCODE =
        "Array: regular hashCode() used instead of Arrays.hashCode() for field";
    private static final String MULTIDIMENSIONAL_EQUALS =
        "Multidimensional array: ==, regular equals() or Arrays.equals() used instead of Arrays.deepEquals() for field";
    private static final String MULTIDIMENSIONAL_HASHCODE =
        "Multidimensional array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field";
    private static final String FIELD_NAME = "array";

    @Test
    public void fail_whenRegularEqualsIsUsedInsteadOfArraysEquals_givenAPrimitiveArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(PrimitiveArrayRegularEquals.class).verify())
            .assertFailure()
            .assertMessageContains(REGULAR_EQUALS, FIELD_NAME);
    }

    @Test
    public void fail_whenRegularHashCodeIsUsedInsteadOfArraysHashCode_givenAPrimitiveArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(PrimitiveArrayRegularHashCode.class).verify())
            .assertFailure()
            .assertMessageContains(REGULAR_HASHCODE, FIELD_NAME);
    }

    @Test
    public void succeed_whenCorrectMethodsAreUsed_givenAPrimitiveArray() {
        EqualsVerifier.forClass(PrimitiveArrayCorrect.class).verify();
    }

    @Test
    public void fail_whenArraysEqualsIsUsedInsteadOfDeepEquals_givenAMultidimensionalArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(MultidimensionalArrayArraysEquals.class).verify())
            .assertFailure()
            .assertMessageContains(MULTIDIMENSIONAL_EQUALS, FIELD_NAME);
    }

    @Test
    public void fail_whenRegularHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
        ExpectedException
            .when(
                () -> EqualsVerifier.forClass(MultidimensionalArrayRegularHashCode.class).verify()
            )
            .assertFailure()
            .assertMessageContains(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
    }

    @Test
    public void fail_whenArraysHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(MultidimensionalArrayArraysHashCode.class).verify())
            .assertFailure()
            .assertMessageContains(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
    }

    @Test
    public void succeed_whenCorrectMethodsAreUsed_givenAMultidimensionalArray() {
        EqualsVerifier.forClass(MultidimensionalArrayCorrect.class).verify();
    }

    @Test
    public void failWithCorrectMessage_whenShallowHashCodeIsUsedOnSecondArray_givenTwoMultidimensionalArrays() {
        ExpectedException
            .when(
                () ->
                    EqualsVerifier
                        .forClass(TwoMultidimensionalArraysShallowHashCodeForSecond.class)
                        .verify()
            )
            .assertFailure()
            .assertMessageContains("second", MULTIDIMENSIONAL_HASHCODE);
    }

    @Test
    public void succeed_whenCorrectMethodsAreUsed_givenAThreedimensionalArray() {
        EqualsVerifier.forClass(ThreeDimensionalArrayCorrect.class).verify();
    }

    @Test
    public void failWithRecursionError_whenClassContainsARecursionButAlsoAMutltiDimensionalArray() {
        ExpectedException
            .when(
                () ->
                    EqualsVerifier.forClass(MultiDimensionalArrayAndRecursion.Board.class).verify()
            )
            .assertThrows(AssertionError.class)
            .assertMessageContains("Recursive datastructure");
    }

    @Test
    public void fail_whenRegularEqualsIsUsedInsteadOfArraysEquals_givenAnObjectArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ObjectArrayRegularEquals.class).verify())
            .assertFailure()
            .assertMessageContains(REGULAR_EQUALS, FIELD_NAME);
    }

    @Test
    public void fail_whenRegularHashCodeIsUsedInsteadOfArraysHashCode_givenAnObjectArray() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ObjectArrayRegularHashCode.class).verify())
            .assertFailure()
            .assertMessageContains(REGULAR_HASHCODE, FIELD_NAME);
    }

    @Test
    public void succeed_whenCorrectMethodsAreUsed_givenAnObjectArray() {
        EqualsVerifier.forClass(ObjectArrayCorrect.class).verify();
    }

    @Test
    public void succeed_whenCorrectMethodsAreUsed_givenAnArrayAndAnUnusedField() {
        EqualsVerifier
            .forClass(ArrayAndSomethingUnused.class)
            .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
            .verify();
    }

    @Test
    public void succeed_whenArraysAreNotUsedInEquals_givenArrayFields() {
        EqualsVerifier
            .forClass(ArraysAreUnused.class)
            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
            .verify();
    }

    @Test
    public void succeed_whenStaticFinalArrayIsEmpty() {
        EqualsVerifier.forClass(EmptyStaticFinalArrayContainer.class).verify();
    }

    @Test
    public void succeed_whenStaticArrayIsUninitialized() {
        EqualsVerifier.forClass(UninitializedStaticArrayContainer.class).verify();
    }

    @Test
    public void succeed_whenArrayLengthIsInvariant() {
        int[] a = { 1, 2, 3 };
        int[] b = { 4, 5, 6 };
        int[][] x = { a, a, a };
        int[][] y = { b, b, b };
        EqualsVerifier
            .forClass(Invariant.class)
            .withPrefabValues(int[].class, a, b)
            .withPrefabValues(int[][].class, x, y)
            .verify();
    }

    static final class PrimitiveArrayRegularEquals {

        private final int[] array;

        public PrimitiveArrayRegularEquals(int[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PrimitiveArrayRegularEquals)) {
                return false;
            }
            PrimitiveArrayRegularEquals other = (PrimitiveArrayRegularEquals) obj;
            return Objects.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.hashCode(array);
        }
    }

    static final class PrimitiveArrayRegularHashCode {

        private final int[] array;

        public PrimitiveArrayRegularHashCode(int[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PrimitiveArrayRegularHashCode)) {
                return false;
            }
            PrimitiveArrayRegularHashCode other = (PrimitiveArrayRegularHashCode) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(array);
        }
    }

    static final class PrimitiveArrayCorrect {

        private final int[] array;

        public PrimitiveArrayCorrect(int[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PrimitiveArrayCorrect)) {
                return false;
            }
            PrimitiveArrayCorrect other = (PrimitiveArrayCorrect) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.hashCode(array);
        }
    }

    static final class MultidimensionalArrayArraysEquals {

        private final int[][] array;

        public MultidimensionalArrayArraysEquals(int[][] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArrayArraysEquals)) {
                return false;
            }
            MultidimensionalArrayArraysEquals other = (MultidimensionalArrayArraysEquals) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.deepHashCode(array);
        }
    }

    static final class MultidimensionalArrayRegularHashCode {

        private final int[][] array;

        public MultidimensionalArrayRegularHashCode(int[][] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArrayRegularHashCode)) {
                return false;
            }
            MultidimensionalArrayRegularHashCode other = (MultidimensionalArrayRegularHashCode) obj;
            return Arrays.deepEquals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : array.hashCode();
        }
    }

    static final class MultidimensionalArrayArraysHashCode {

        private final int[][] array;

        public MultidimensionalArrayArraysHashCode(int[][] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArrayArraysHashCode)) {
                return false;
            }
            MultidimensionalArrayArraysHashCode other = (MultidimensionalArrayArraysHashCode) obj;
            return Arrays.deepEquals(array, other.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    static final class MultidimensionalArrayCorrect {

        private final int[][] array;

        public MultidimensionalArrayCorrect(int[][] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultidimensionalArrayCorrect)) {
                return false;
            }
            MultidimensionalArrayCorrect other = (MultidimensionalArrayCorrect) obj;
            return Arrays.deepEquals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.deepHashCode(array);
        }
    }

    static final class TwoMultidimensionalArraysShallowHashCodeForSecond {

        private final Object[][] first;
        private final Object[][] second;

        public TwoMultidimensionalArraysShallowHashCodeForSecond(
            Object[][] first,
            Object[][] second
        ) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoMultidimensionalArraysShallowHashCodeForSecond)) {
                return false;
            }
            TwoMultidimensionalArraysShallowHashCodeForSecond other = (TwoMultidimensionalArraysShallowHashCodeForSecond) obj;
            return Arrays.deepEquals(first, other.first) && Arrays.deepEquals(second, other.second);
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = (result * prime) + Arrays.deepHashCode(first);
            result = (result * prime) + Arrays.hashCode(second);
            return result;
        }
    }

    static final class ThreeDimensionalArrayCorrect {

        private final int[][][] array;

        public ThreeDimensionalArrayCorrect(int[][][] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeDimensionalArrayCorrect)) {
                return false;
            }
            ThreeDimensionalArrayCorrect other = (ThreeDimensionalArrayCorrect) obj;
            return Arrays.deepEquals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.deepHashCode(array);
        }
    }

    @SuppressWarnings("unused")
    static final class MultiDimensionalArrayAndRecursion {

        static final class Board {

            private final Object[][] grid;
            private BoardManipulator manipulator = new BoardManipulator(this);

            public Board(Object[][] grid) {
                this.grid = grid;
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }

        static final class BoardManipulator {

            private final Board board;

            public BoardManipulator(Board board) {
                this.board = board;
            }
        }
    }

    static final class ObjectArrayRegularEquals {

        private final Object[] array;

        public ObjectArrayRegularEquals(Object[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ObjectArrayRegularEquals)) {
                return false;
            }
            ObjectArrayRegularEquals other = (ObjectArrayRegularEquals) obj;
            return Objects.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.hashCode(array);
        }
    }

    static final class ObjectArrayRegularHashCode {

        private final Object[] array;

        public ObjectArrayRegularHashCode(Object[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ObjectArrayRegularHashCode)) {
                return false;
            }
            ObjectArrayRegularHashCode other = (ObjectArrayRegularHashCode) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(array);
        }
    }

    static final class ObjectArrayCorrect {

        private final Object[] array;

        public ObjectArrayCorrect(Object[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ObjectArrayCorrect)) {
                return false;
            }
            ObjectArrayCorrect other = (ObjectArrayCorrect) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return (array == null) ? 0 : Arrays.hashCode(array);
        }
    }

    static final class ArrayAndSomethingUnused {

        private final int[] array;

        @SuppressWarnings("unused")
        private final int i;

        public ArrayAndSomethingUnused(int[] array, int i) {
            this.array = array;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayAndSomethingUnused)) {
                return false;
            }
            ArrayAndSomethingUnused other = (ArrayAndSomethingUnused) obj;
            return Arrays.equals(array, other.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    @SuppressWarnings("unused")
    static final class ArraysAreUnused {

        private final int[] ints;
        private final Object[] objects;
        private final int[][] arrays;

        public ArraysAreUnused(int[] ints, Object[] objects, int[][] arrays) {
            this.ints = ints;
            this.objects = objects;
            this.arrays = arrays;
        }
    }

    static final class EmptyStaticFinalArrayContainer {

        public static final EmptyStaticFinalArrayContainer[] EMPTY = {};

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused")
    static final class UninitializedStaticArrayContainer {

        public static int[] uninitialized;

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    public static final class Invariant {

        private final int[] array;
        private final int[][] multiArray;

        public Invariant(int[] array, int[][] multiArray) {
            this.array = array;
            this.multiArray = multiArray;
        }

        @Override
        public boolean equals(Object obj) {
            assert array == null || array.length == 3 : "Invariant broken";
            assert multiArray == null || multiArray.length == 3 : "Invariant broken";

            if (!(obj instanceof Invariant)) {
                return false;
            }
            Invariant other = (Invariant) obj;
            return (
                Arrays.equals(array, other.array) && Arrays.deepEquals(multiArray, other.multiArray)
            );
        }

        @Override
        public int hashCode() {
            int result = 41;
            result = 41 * result + Arrays.hashCode(array);
            result = 41 * result + Arrays.deepHashCode(multiArray);
            return result;
        }
    }
}
