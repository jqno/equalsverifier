package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Arrays;
import java.util.Objects;

import nl.jqno.equalsverifier_testhelpers.annotations.*;

public class TypeHelper {

    private static final Object OBJECT = new Object();

    public enum SimpleEnum {
        FIRST, SECOND
    }

    public static final class AllTypesContainer {

        // CHECKSTYLE OFF: MemberName
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

        public SimpleEnum _enum = SimpleEnum.FIRST;
        public int[] _array = { 1, 2, 3 };
        public Object _object = OBJECT;
        public String _string = "";

        // CHECKSTYLE ON: MemberName

        @Override
        @SuppressWarnings("EqualsHashCode")
        public boolean equals(Object obj) {
            if (!(obj instanceof AllTypesContainer)) {
                return false;
            }
            AllTypesContainer other = (AllTypesContainer) obj;
            boolean result = true;
            result &= _boolean == other._boolean;
            result &= _byte == other._byte;
            result &= _char == other._char;
            result &= Double.compare(_double, other._double) == 0;
            result &= Float.compare(_float, other._float) == 0;
            result &= _int == other._int;
            result &= _long == other._long;
            result &= _short == other._short;
            result &= Objects.equals(_Boolean, other._Boolean);
            result &= Objects.equals(_Byte, other._Byte);
            result &= Objects.equals(_Char, other._Char);
            result &= _Double == null ? other._Double == null : Double.compare(_Double, other._Double) == 0;
            result &= _Float == null ? other._Float == null : Float.compare(_Float, other._Float) == 0;
            result &= Objects.equals(_Int, other._Int);
            result &= Objects.equals(_Long, other._Long);
            result &= Objects.equals(_Short, other._Short);
            result &= _enum == other._enum;
            result &= Arrays.equals(_array, other._array);
            result &= Objects.equals(_object, other._object);
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

        // CHECKSTYLE OFF: MemberName
        Boolean[] Booleans = { true };
        Byte[] Bytes = { 1 };
        Character[] Characters = { 'a' };
        Double[] Doubles = { 1.0D };
        Float[] Floats = { 1.0F };
        Integer[] Integers = { 1 };
        Long[] Longs = { 1L };
        Short[] Shorts = { 1 };
        // CHECKSTYLE ON: MemberName

        SimpleEnum[] enums = { SimpleEnum.FIRST };
        int[][] arrays = { { 1 } };
        Object[] objects = { OBJECT };
        String[] strings = { "1" };

        @Override
        @SuppressWarnings("EqualsHashCode")
        public boolean equals(Object obj) {
            if (!(obj instanceof AllArrayTypesContainer)) {
                return false;
            }
            AllArrayTypesContainer other = (AllArrayTypesContainer) obj;
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

    @SuppressWarnings("unused")
    public static class UnorderedFieldContainer {

        public static final int THREE = 3;
        public final int one = 1;
        private static final int FOUR = 4;
        private final int two = 2;
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

    public static final class GenericTypeVariableContainerContainer<T> {

        public GenericTypeVariableContainer<T> container = new GenericTypeVariableContainer<>();
    }

    public static final class GenericTypeVariableContainer<T> {

        public T t = null;
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

    public static class ArrayContainer {

        int[] field;
    }

    public static final class PrimitiveContainer {

        public int field;
    }

    public static final class NonNullContainer {

        @NonNull
        public Object field = new Object();
    }

    public static final class StaticFinalContainer {

        public static final int CONST = 42;
        public static final Object OBJECT = new Object();
    }

    public static class NoFields {}

    public static class NoFieldsSubWithFields extends NoFields {

        public Object field;
    }

    public enum TwoElementEnum {
        ONE, TWO
    }

    public enum OneElementEnum {
        ONE
    }

    public enum EmptyEnum {}

    @SuppressWarnings("unused")
    public static final class EnumContainer {

        private EmptyEnum emptyEnum;
        private OneElementEnum oneElementEnum;
        private TwoElementEnum twoElementEnum;
    }

    @SuppressWarnings("unused")
    public static final class SingleGenericContainerContainer {

        private final SingleGenericContainer<String> string;
        private final SingleGenericContainer<Integer> integer;

        public SingleGenericContainerContainer(
                SingleGenericContainer<String> string,
                SingleGenericContainer<Integer> integer) {
            this.string = string;
            this.integer = integer;
        }

        @Override
        public boolean equals(Object obj) {
            if (string != null) {
                String s = string.t;
            }
            if (!(obj instanceof SingleGenericContainerContainer)) {
                return false;
            }
            var other = (SingleGenericContainerContainer) obj;
            return Objects.equals(string, other.string) && Objects.equals(integer, other.integer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, integer);
        }
    }

    public static final class SingleGenericContainer<T> {

        private final SingleGenericContainer<Void> justToMakeItRecursiveAndForcePrefabValues = null;

        private final T t;

        public SingleGenericContainer(T t) {
            this.t = t;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SingleGenericContainer)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            var other = (SingleGenericContainer<T>) obj;
            return Objects
                    .equals(justToMakeItRecursiveAndForcePrefabValues, other.justToMakeItRecursiveAndForcePrefabValues)
                    && Objects.equals(t, other.t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(justToMakeItRecursiveAndForcePrefabValues, t);
        }
    }

    @SuppressWarnings("unused")
    public static final class DoubleGenericContainerContainer {

        private final DoubleGenericContainer<String, Boolean> stringBoolean;
        private final DoubleGenericContainer<Integer, Byte> integerByte;

        public DoubleGenericContainerContainer(
                DoubleGenericContainer<String, Boolean> stringBoolean,
                DoubleGenericContainer<Integer, Byte> integerByte) {
            this.stringBoolean = stringBoolean;
            this.integerByte = integerByte;
        }

        @Override
        public boolean equals(Object obj) {
            if (stringBoolean != null) {
                String s = stringBoolean.t;
            }
            if (!(obj instanceof DoubleGenericContainerContainer)) {
                return false;
            }
            var other = (DoubleGenericContainerContainer) obj;
            return Objects.equals(stringBoolean, other.stringBoolean) && Objects.equals(integerByte, other.integerByte);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringBoolean, integerByte);
        }
    }

    public static final class DoubleGenericContainer<T, U> {

        private final DoubleGenericContainer<Void, Void> justToMakeItRecursiveAndForcePrefabValues = null;

        private final T t;
        private final U u;

        public DoubleGenericContainer(T t, U u) {
            this.t = t;
            this.u = u;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DoubleGenericContainer)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            var other = (DoubleGenericContainer<T, U>) obj;
            return Objects
                    .equals(justToMakeItRecursiveAndForcePrefabValues, other.justToMakeItRecursiveAndForcePrefabValues)
                    && Objects.equals(t, other.t)
                    && Objects.equals(u, other.u);
        }

        @Override
        public int hashCode() {
            return Objects.hash(justToMakeItRecursiveAndForcePrefabValues, t, u);
        }
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

    public static class AnnotatedTypes {

        @TypeUseAnnotationRuntimeRetention
        public int runtimeRetention;

        @TypeUseAnnotationClassRetention
        public int classRetention;

        @TypeUseAnnotationRuntimeRetention
        @TypeUseAnnotationClassRetention
        public int bothRetentions;

        public int noRetention;
    }

    public static class AnnotatedMethods {

        private int runtimeRetention;
        private int classRetention;
        private int bothRetentions;
        private int noRetention;

        @MethodAnnotationRuntimeRetention
        public int getRuntimeRetention() {
            return runtimeRetention;
        }

        @MethodAnnotationClassRetention
        public int getClassRetention() {
            return classRetention;
        }

        @MethodAnnotationRuntimeRetention
        @MethodAnnotationClassRetention
        public int getBothRetentions() {
            return bothRetentions;
        }

        public int getNoRetention() {
            return noRetention;
        }
    }

    @TypeAnnotationInherits
    @TypeAnnotationDoesntInherit
    public static class SuperclassWithAnnotations {

        @FieldAnnotationInherits
        @TypeUseAnnotationInherits
        public int inherits;

        @FieldAnnotationDoesntInherit
        @TypeUseAnnotationDoesntInherit
        public int doesntInherit;
    }

    public static class SubclassWithAnnotations extends SuperclassWithAnnotations {}

    @Inapplicable
    public static class InapplicableAnnotations {

        @Inapplicable
        public int inapplicable;
    }

    public static class PostProcessedFieldAnnotation {

        @PostProcess
        public int postProcessed;
    }

    public abstract static class AbstractEqualsAndHashCode {

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    public static final class ClassContainsMethodNamedGet {

        public int get() {
            return 1;
        }
    }

    @DifficultAnnotation
    public static final class DifficultAnnotationHolder {

        @DifficultAnnotation
        private int i;

        @DifficultAnnotation
        public int getI() {
            return i;
        }
    }

    public static final class LoadedBySystemClassLoader extends Exception {}
}
