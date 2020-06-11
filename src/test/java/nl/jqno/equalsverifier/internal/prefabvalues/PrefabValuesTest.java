package nl.jqno.equalsverifier.internal.prefabvalues;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PrefabValuesTest {
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);
    private static final TypeTag STRING_ARRAY_TAG = new TypeTag(String[].class);

    @Rule public ExpectedException thrown = ExpectedException.none();

    private FactoryCache factoryCache = new FactoryCache();
    private PrefabValues pv;

    @Before
    public void setUp() {
        factoryCache.put(String.class, new AppendingStringTestFactory());
        factoryCache.put(int.class, values(42, 1337, 42));
        pv = new PrefabValues(factoryCache);
    }

    @Test
    public void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertEquals("r", f.createValues(null, null, null).getRed());
        assertEquals("rr", f.createValues(null, null, null).getRed());
        assertEquals("rrr", f.createValues(null, null, null).getRed());
    }

    @Test
    public void giveRedFromFactory() {
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    @Test
    public void giveRedFromCache() {
        pv.giveRed(STRING_TAG);
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    @Test
    public void giveBlueFromFactory() {
        assertEquals("b", pv.giveBlue(STRING_TAG));
    }

    @Test
    public void giveBlueFromCache() {
        pv.giveBlue(STRING_TAG);
        assertEquals("b", pv.giveBlue(STRING_TAG));
    }

    @Test
    public void giveRedCopyFromFactory() {
        assertEquals("r", pv.giveRedCopy(STRING_TAG));
        assertNotSame(pv.giveRed(STRING_TAG), pv.giveRedCopy(STRING_TAG));
    }

    @Test
    public void giveRedCopyFromCache() {
        pv.giveRedCopy(STRING_TAG);
        assertEquals("r", pv.giveRedCopy(STRING_TAG));
        assertNotSame(pv.giveRed(STRING_TAG), pv.giveRedCopy(STRING_TAG));
    }

    @Test
    public void giveRedFromFallbackFactory() {
        Point actual = pv.giveRed(POINT_TAG);
        assertEquals(new Point(42, 42), actual);
    }

    @Test
    public void giveBlueFromFallbackFactory() {
        Point actual = pv.giveBlue(POINT_TAG);
        assertEquals(new Point(1337, 1337), actual);
    }

    @Test
    public void giveRedCopyFromFallbackFactory() {
        Point actual = pv.giveRedCopy(POINT_TAG);
        assertEquals(new Point(42, 42), actual);
        assertNotSame(pv.giveRed(POINT_TAG), actual);
    }

    @Test
    public void giveTuple() {
        Tuple<Point> actual = pv.giveTuple(POINT_TAG);
        assertEquals(Tuple.of(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)), actual);
    }

    @Test
    public void giveOtherWhenValueIsKnown() {
        Point red = pv.giveRed(POINT_TAG);
        Point blue = pv.giveBlue(POINT_TAG);
        assertEquals(blue, pv.giveOther(POINT_TAG, red));
        assertEquals(red, pv.giveOther(POINT_TAG, blue));
    }

    @Test
    public void giveOtherWhenValueIsCloneOfKnown() {
        Point red = new Point(42, 42);
        Point blue = new Point(1337, 1337);
        assertEquals(blue, pv.giveOther(POINT_TAG, red));
        assertEquals(red, pv.giveOther(POINT_TAG, blue));

        // Sanity check
        assertEquals(red, pv.giveRed(POINT_TAG));
        assertEquals(blue, pv.giveBlue(POINT_TAG));
    }

    @Test
    public void giveOtherWhenValueIsUnknown() {
        Point value = new Point(-1, -1);
        Point expected = pv.giveRed(POINT_TAG);
        assertEquals(expected, pv.giveOther(POINT_TAG, value));
    }

    @Test
    public void giveOtherWhenValueIsPrimitive() {
        int expected = pv.giveRed(INT_TAG);
        assertEquals(expected, (int) pv.giveOther(INT_TAG, -10));
    }

    @Test
    public void giveOtherWhenValueIsNull() {
        Point expected = pv.giveRed(POINT_TAG);
        assertEquals(expected, pv.giveOther(POINT_TAG, null));
    }

    @Test
    public void giveOtherWhenValueIsKnownArray() {
        String[] red = pv.giveRed(STRING_ARRAY_TAG);
        String[] blue = pv.giveBlue(STRING_ARRAY_TAG);
        assertArrayEquals(blue, pv.giveOther(STRING_ARRAY_TAG, red));
        assertArrayEquals(red, pv.giveOther(STRING_ARRAY_TAG, blue));
    }

    @Test
    public void giveOtherWhenValueIsCloneOfKnownArray() {
        String[] red = {"r"};
        String[] blue = {"b"};
        assertArrayEquals(blue, pv.giveOther(STRING_ARRAY_TAG, red));
        assertArrayEquals(red, pv.giveOther(STRING_ARRAY_TAG, blue));

        // Sanity check
        assertArrayEquals(red, pv.<String[]>giveRed(STRING_ARRAY_TAG));
        assertArrayEquals(blue, pv.<String[]>giveBlue(STRING_ARRAY_TAG));
    }

    @Test
    public void giveOtherWhenValueIsUnknownArray() {
        String[] value = {"hello world"};
        String[] expected = pv.giveRed(STRING_ARRAY_TAG);
        assertArrayEquals(expected, pv.giveOther(STRING_ARRAY_TAG, value));
    }

    @Test
    public void giveOtherWhenTagDoesntMatchValue() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("TypeTag does not match value.");
        pv.giveOther(POINT_TAG, "not a Point");
    }

    @Test
    public void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        pv.giveRed(new TypeTag(StaticContainer.class));
        assertEquals(expected, StaticContainer.staticInt);
    }

    @Test
    public void stringListIsSeparateFromIntegerList() {
        factoryCache.put(List.class, new ListTestFactory());
        pv = new PrefabValues(factoryCache);

        List<String> strings = pv.giveRed(new TypeTag(List.class, STRING_TAG));
        List<Integer> ints = pv.giveRed(new TypeTag(List.class, INT_TAG));

        assertEquals("r", strings.get(0));
        assertEquals(42, (int) ints.get(0));
    }

    @Test
    public void addingNullDoesntBreakAnything() {
        factoryCache.put((Class<?>) null, new ListTestFactory());
    }

    @Test
    public void addingATypeTwiceOverrulesTheExistingOne() {
        factoryCache.put(int.class, values(-1, -2, -1));
        pv = new PrefabValues(factoryCache);
        assertEquals(-1, (int) pv.giveRed(INT_TAG));
        assertEquals(-2, (int) pv.giveBlue(INT_TAG));
    }

    @Test
    public void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        factoryCache.put(Lazy.class.getName(), values(Lazy.X, Lazy.Y, Lazy.X));
        pv = new PrefabValues(factoryCache);
        assertEquals(Lazy.X, pv.giveRed(lazyTag));
        assertEquals(Lazy.Y, pv.giveBlue(lazyTag));
        assertEquals(Lazy.X, pv.giveRedCopy(lazyTag));
    }

    @Test
    public void addLazyFactoryIsLazy() {
        TypeTag throwingLazyTag = new TypeTag(ThrowingLazy.class);

        // Doesn't throw:
        factoryCache.put(
                ThrowingLazy.class.getName(),
                (tag, prefabValues, typeStack) ->
                        Tuple.of(ThrowingLazy.X, ThrowingLazy.Y, ThrowingLazy.X));
        pv = new PrefabValues(factoryCache);

        // Does throw:
        try {
            pv.giveRed(throwingLazyTag);
            fail("Expected an exception");
        } catch (ExceptionInInitializerError e) {
            // succeed
        }
    }

    private static class AppendingStringTestFactory implements PrefabValueFactory<String> {
        private String red;
        private String blue;

        public AppendingStringTestFactory() {
            red = "";
            blue = "";
        }

        @Override
        public Tuple<String> createValues(
                TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            red += "r";
            blue += "b";
            return new Tuple<>(red, blue, new String(red));
        }
    }

    @SuppressWarnings("rawtypes")
    private static class ListTestFactory implements PrefabValueFactory<List> {
        @Override
        @SuppressWarnings("unchecked")
        public Tuple<List> createValues(
                TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            TypeTag subtag = tag.getGenericTypes().get(0);

            List red = new ArrayList<>();
            red.add(prefabValues.giveRed(subtag));

            List blue = new ArrayList<>();
            blue.add(prefabValues.giveBlue(subtag));

            List redCopy = new ArrayList<>();
            redCopy.add(prefabValues.giveRed(subtag));

            return new Tuple<>(red, blue, redCopy);
        }
    }

    private static class StaticContainer {
        static int staticInt = 2;

        @SuppressWarnings("unused")
        int regularInt = 3;
    }

    @SuppressWarnings("unused")
    public static class Lazy {
        public static final Lazy X = new Lazy(1);
        public static final Lazy Y = new Lazy(2);

        private final int i;

        public Lazy(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }

        @Override
        public String toString() {
            return "Lazy: " + i;
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowingLazy {
        {
            if (true) {
                throw new IllegalStateException("initializing");
            }
        }

        public static final ThrowingLazy X = new ThrowingLazy();
        public static final ThrowingLazy Y = new ThrowingLazy();
    }
}
