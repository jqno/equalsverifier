package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Objects;

import nl.jqno.equalsverifier_testhelpers.annotations.*;

public class TypeHelper {

    public enum SimpleEnum {
        FIRST, SECOND
    }

    public static final class AllTypesContainer {

        // CHECKSTYLE OFF: MemberName
        public boolean _boolean;
        public byte _byte;
        public char _char;
        public double _double;
        public float _float;
        public int _int;
        public long _long;
        public short _short;

        public Boolean _Boolean;
        public Byte _Byte;
        public Character _Char;
        public Double _Double;
        public Float _Float;
        public Integer _Int;
        public Long _Long;
        public Short _Short;

        public SimpleEnum _enum;
        public int[] _array;
        public Object _object;
        public String _string;
        // CHECKSTYLE ON: MemberName
    }

    public static final class AllArrayTypesContainer {

        public boolean[] booleans;
        public byte[] bytes;
        public char[] chars;
        public double[] doubles;
        public float[] floats;
        public int[] ints;
        public long[] longs;
        public short[] shorts;

        // CHECKSTYLE OFF: MemberName
        public Boolean[] Booleans;
        public Byte[] Bytes;
        public Character[] Characters;
        public Double[] Doubles;
        public Float[] Floats;
        public Integer[] Integers;
        public Long[] Longs;
        public Short[] Shorts;
        // CHECKSTYLE ON: MemberName

        public SimpleEnum[] enums;
        public int[][] arrays;
        public Object[] objects;
        public String[] strings;
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
