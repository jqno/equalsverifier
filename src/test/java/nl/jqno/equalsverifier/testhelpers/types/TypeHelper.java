package nl.jqno.equalsverifier.testhelpers.types;

import nl.jqno.equalsverifier.testhelpers.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TypeHelper {
    private static final Object OBJECT = new Object();

    public enum Enum { FIRST, SECOND }

    public static final class AllTypesContainer {
        // CHECKSTYLE: ignore MemberName for 23 lines.
        public boolean _boolean = false;
        public byte _byte = 0;
        public char _char = '\u0000';
        public double _double = 0.0D;
        public float _float = 0.0F;
        public int _int = 0;
        public long _long = 0L;
        public short _short = 0;

        public Boolean _Boolean = false;
        public Byte _Byte = 0;
        public Character _Char = '\u0000';
        public Double _Double = 0.0D;
        public Float _Float = 0.0F;
        public Integer _Int = 0;
        public Long _Long = 0L;
        public Short _Short = 0;

        public Enum _enum = Enum.FIRST;
        public int[] _array = {1, 2, 3};
        public Object _object = OBJECT;
        public Class<?> _type = Class.class;
        public String _string = "";

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AllTypesContainer)) {
                return false;
            }
            AllTypesContainer other = (AllTypesContainer)obj;
            boolean result = true;
            result &= _boolean == other._boolean;
            result &= _byte == other._byte;
            result &= _char == other._char;
            result &= Double.compare(_double, other._double) == 0;
            result &= Float.compare(_float, other._float) == 0;
            result &= _int == other._int;
            result &= _long == other._long;
            result &= _short == other._short;
            result &= _Boolean == other._Boolean;
            result &= Objects.equals(_Byte, other._Byte);
            result &= _Char == other._Char;
            result &= _Double == null ? other._Double == null : Double.compare(_Double, other._Double) == 0;
            result &= _Float == null ? other._Float == null : Float.compare(_Float, other._Float) == 0;
            result &= Objects.equals(_Int, other._Int);
            result &= Objects.equals(_Long, other._Long);
            result &= Objects.equals(_Short, other._Short);
            result &= _enum == other._enum;
            result &= Arrays.equals(_array, other._array);
            result &= Objects.equals(_object, other._object);
            result &= _type == other._type;
            result &= Objects.equals(_string, other._string);
            return result;
        }
    }

    public static final class AllArrayTypesContainer {
        boolean[] booleans = { true };
        byte[] bytes = { 1 };
        char[] chars = { 'a' };
        double[] doubles = { 1.0D };
        float[] floats = { 1.0F };
        int[] ints = { 1 };
        long[] longs = { 1L };
        short[] shorts = { 1 };

        // CHECKSTYLE: ignore MemberName for 8 lines.
        Boolean[] Booleans = { true };
        Byte[] Bytes = { 1 };
        Character[] Characters = { 'a' };
        Double[] Doubles = { 1.0D };
        Float[] Floats = { 1.0F };
        Integer[] Integers = { 1 };
        Long[] Longs = { 1L };
        Short[] Shorts = { 1 };

        Enum[] enums = { Enum.FIRST };
        int[][] arrays = { { 1 } };
        Object[] objects = { OBJECT };
        Class<?>[] classes = { Class.class };
        String[] strings = { "1" };

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AllArrayTypesContainer)) {
                return false;
            }
            AllArrayTypesContainer other = (AllArrayTypesContainer)obj;
            boolean result = true;
            result &= Arrays.equals(booleans, other.booleans);
            result &= Arrays.equals(bytes, other.bytes);
            result &= Arrays.equals(chars, other.chars);
            result &= Arrays.equals(doubles, other.doubles);
            result &= Arrays.equals(floats, other.floats);
            result &= Arrays.equals(ints, other.ints);
            result &= Arrays.equals(longs, other.longs);
            result &= Arrays.equals(shorts, other.shorts);
            result &= Arrays.equals(Booleans, other.Booleans);
            result &= Arrays.equals(Bytes, other.Bytes);
            result &= Arrays.equals(Characters, other.Characters);
            result &= Arrays.equals(Doubles, other.Doubles);
            result &= Arrays.equals(Floats, other.Floats);
            result &= Arrays.equals(Integers, other.Integers);
            result &= Arrays.equals(Longs, other.Longs);
            result &= Arrays.equals(Shorts, other.Shorts);
            result &= Arrays.equals(enums, other.enums);
            result &= Arrays.deepEquals(arrays, other.arrays);
            result &= Arrays.equals(objects, other.objects);
            result &= Arrays.equals(classes, other.classes);
            result &= Arrays.equals(strings, other.strings);
            return result;
        }
    }

    @SuppressWarnings("unused")
    public static class DifferentAccessModifiersFieldContainer {
        public static final int L = 0;
        protected static final int K = 0;
        static final int J = 0;
        private static final int I = 0;

        public final int l = 0;
        protected final int k = 0;
        final int j = 0;
        private final int i = 0;
    }

    @SuppressWarnings("unused")
    public static class DifferentAccessModifiersSubFieldContainer extends DifferentAccessModifiersFieldContainer {
        public final String d = "";
        protected final String c = "";
        final String b = "";
        private final String a = "";
    }

    public static class EmptySubFieldContainer extends DifferentAccessModifiersFieldContainer {}

    public static class SubEmptySubFieldContainer extends EmptySubFieldContainer {
        public long field = 0;
    }

    public interface Interface {}

    public abstract static class AbstractClass {
        int field;
    }

    public static class Outer {
        public class Inner {
            public Outer getOuter() {
                return Outer.this;
            }
        }
    }

    public static class Empty {}

    public static final class InterfaceContainer {
        public Interface field;
    }

    public static final class AbstractClassContainer {
        public AbstractClass field;
    }

    public static final class AbstractAndInterfaceArrayContainer {
        public AbstractClass[] abstractClasses = new AbstractClass[] { null };
        public Interface[] interfaces = new Interface[] { null };
    }

    public static final class GenericListContainer {
        public List<String> stringList = new ArrayList<>();
        public List<Integer> integerList = new ArrayList<>();
    }

    public static final class GenericTypeVariableListContainer<T> {
        public List<T> tList = new ArrayList<>();
    }

    public static final class ObjectContainer {
        public Object field = new Object();
    }

    public static final class FinalContainer {
        final Object field = new Object();
    }

    public static final class StaticContainer {
        public static Object field = new Object();
    }

    public static final class TransientContainer {
        transient Object field = new Object();
    }

    public static class PrivateObjectContainer {
        private Object field = new Object();

        public Object get() {
            return field;
        }
    }

    public static class ArrayContainer {
        int[] field;
    }

    public static final class PrimitiveContainer {
        public int field;
    }

    public static final class StaticFinalContainer {
        public static final int CONST = 42;
        public static final Object OBJECT = new Object();
    }

    public static class NoFields {}

    public static class NoFieldsSubWithFields extends NoFields {
        public Object field;
    }

    public enum TwoElementEnum { ONE, TWO }

    public enum OneElementEnum { ONE }

    public enum EmptyEnum {}

    @SuppressWarnings("unused")
    public static final class EnumContainer {
        private OneElementEnum oneElementEnum;
        private TwoElementEnum twoElementEnum;
    }

    public static final class PointArrayContainer {
        public Point[] points = { new Point(1, 2) };
    }

    @TypeAnnotationRuntimeRetention
    public static class AnnotatedWithRuntime {}

    @TypeAnnotationClassRetention
    public static class AnnotatedWithClass {}

    @TypeAnnotationRuntimeRetention
    @TypeAnnotationClassRetention
    public static class AnnotatedWithBoth {}

    @TypeAnnotationClassRetention
    public static class AnnotatedOuter {
        public static class AnnotatedMiddle {
            public static class AnnotatedInner {}
        }
    }

    public static class AnnotatedFields {
        @FieldAnnotationRuntimeRetention
        public int runtimeRetention;

        @FieldAnnotationClassRetention
        public int classRetention;

        @FieldAnnotationRuntimeRetention
        @FieldAnnotationClassRetention
        public int bothRetentions;

        public int noRetention;
    }

    @TypeAnnotationInherits
    @TypeAnnotationDoesntInherit
    public static class SuperclassWithAnnotations {
        @FieldAnnotationInherits
        public int inherits;

        @FieldAnnotationDoesntInherit
        public int doesntInherit;
    }

    public static class SubclassWithAnnotations extends SuperclassWithAnnotations {}

    @Inapplicable
    public static class InapplicableAnnotations {
        @Inapplicable
        public int inapplicable;
    }

    public abstract static class AbstractEqualsAndHashCode {
        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    @SuppressWarnings("serial")
    public static final class LoadedBySystemClassLoader extends Exception {}
}
