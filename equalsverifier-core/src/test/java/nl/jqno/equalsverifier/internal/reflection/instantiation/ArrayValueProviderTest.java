package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProvider;
import org.junit.jupiter.api.Test;

public class ArrayValueProviderTest {

    private ArrayValueProvider sut = new ArrayValueProvider(TestValueProvider.INSTANCE);

    @Test
    public void singleDimensionalArray() {
        TypeTag tag = new TypeTag(int[].class);
        Tuple<int[]> expected = TestValueProvider.INTS.map(i -> new int[] { i });
        Tuple<int[]> actual = sut.provide(tag);

        // Can't use Tuple.equals because the values are arrays
        assertAll(
            () -> assertArrayEquals(expected.getRed(), actual.getRed()),
            () -> assertArrayEquals(expected.getBlue(), actual.getBlue()),
            () -> assertArrayEquals(expected.getRedCopy(), actual.getRedCopy())
        );
    }

    @Test
    public void twoDimensionalArray() {
        TypeTag tag = new TypeTag(String[][].class);
        Tuple<String[][]> expected = TestValueProvider.STRINGS.map(s -> new String[][] { { s } });
        Tuple<String[][]> actual = sut.provide(tag);

        // Can't use Tuple.equals because the values are arrays
        assertAll(
            () -> assertArrayEquals(expected.getRed(), actual.getRed()),
            () -> assertArrayEquals(expected.getBlue(), actual.getBlue()),
            () -> assertArrayEquals(expected.getRedCopy(), actual.getRedCopy())
        );
    }

    @Test
    public void multiDimensionalArray() {
        TypeTag tag = new TypeTag(int[][][].class);
        Tuple<int[][][]> expected = TestValueProvider.INTS.map(i -> new int[][][] { { { i } } });
        Tuple<int[][][]> actual = sut.provide(tag);

        // Can't use Tuple.equals because the values are arrays
        assertAll(
            () -> assertArrayEquals(expected.getRed(), actual.getRed()),
            () -> assertArrayEquals(expected.getBlue(), actual.getBlue()),
            () -> assertArrayEquals(expected.getRedCopy(), actual.getRedCopy())
        );
    }
}
