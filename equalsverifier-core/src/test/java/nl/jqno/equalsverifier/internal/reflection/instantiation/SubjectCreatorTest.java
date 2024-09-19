package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.lang.reflect.Field;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubjectCreatorTest {

    private static final int I_RED = 42;
    private static final int I_BLUE = 1337;
    private static final String S_RED = "abc";
    private static final String S_BLUE = "xyz";

    private Configuration<SomeClass> config = ConfigurationHelper.emptyConfiguration(
        SomeClass.class
    );
    private ValueProvider valueProvider = new SubjectCreatorTestValueProvider();
    private FieldCache fieldCache = new FieldCache();
    private SubjectCreator<SomeClass> sut = new SubjectCreator<>(config, valueProvider, fieldCache);

    private Field fieldX;
    private Field fieldI;
    private Field fieldS;

    private SomeClass expected;
    private SomeClass actual;

    @BeforeEach
    public void setup() throws NoSuchFieldException {
        fieldX = SomeSuper.class.getDeclaredField("x");
        fieldI = SomeClass.class.getDeclaredField("i");
        fieldS = SomeClass.class.getDeclaredField("s");
    }

    @Test
    public void sanity() {}

    @Test
    public void plain() {
        expected = new SomeClass(I_RED, I_RED, S_RED);
        actual = sut.plain();

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldDefaulted_super() {
        expected = new SomeClass(0, I_RED, S_RED);
        actual = sut.withFieldDefaulted(fieldX);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldDefaulted_primitive() {
        expected = new SomeClass(I_RED, 0, S_RED);
        actual = sut.withFieldDefaulted(fieldI);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldDefaulted_object() {
        expected = new SomeClass(I_RED, I_RED, null);
        actual = sut.withFieldDefaulted(fieldS);

        assertEquals(expected, actual);
    }

    @Test
    public void withAllFieldsDefaulted() {
        expected = new SomeClass(0, 0, null);
        actual = sut.withAllFieldsDefaulted();

        assertEquals(expected, actual);
    }

    @Test
    public void withAllFieldsDefaultedExcept() {
        expected = new SomeClass(0, I_RED, null);
        actual = sut.withAllFieldsDefaultedExcept(fieldI);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldSetTo_super() {
        expected = new SomeClass(99, I_RED, S_RED);
        actual = sut.withFieldSetTo(fieldX, 99);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldSetTo_primitive() {
        expected = new SomeClass(I_RED, 99, S_RED);
        actual = sut.withFieldSetTo(fieldI, 99);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldSetTo_object() {
        expected = new SomeClass(I_RED, I_RED, "value");
        actual = sut.withFieldSetTo(fieldS, "value");

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldChanged_super() {
        expected = new SomeClass(I_BLUE, I_RED, S_RED);
        actual = sut.withFieldChanged(fieldX);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldChanged_primitive() {
        expected = new SomeClass(I_RED, I_BLUE, S_RED);
        actual = sut.withFieldChanged(fieldI);

        assertEquals(expected, actual);
    }

    @Test
    public void withFieldChanged_object() {
        expected = new SomeClass(I_RED, I_RED, S_BLUE);
        actual = sut.withFieldChanged(fieldS);

        assertEquals(expected, actual);
    }

    @Test
    public void withAllFieldsChanged() {
        expected = new SomeClass(I_BLUE, I_BLUE, S_BLUE);
        actual = sut.withAllFieldsChanged();

        assertEquals(expected, actual);
    }

    @Test
    public void withAllFieldsShallowlyChanged() {
        expected = new SomeClass(I_RED, I_BLUE, S_BLUE);
        actual = sut.withAllFieldsShallowlyChanged();

        assertEquals(expected, actual);
    }

    @Test
    public void copy() {
        expected = new SomeClass(I_RED, I_RED, S_RED);
        SomeClass original = new SomeClass(I_RED, I_RED, S_RED);
        actual = sut.copy(original);

        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    public void copyIntoSuperclass() {
        SomeSuper superExpected = new SomeSuper(I_RED);
        SomeClass original = new SomeClass(I_RED, I_RED, S_RED);
        Object superActual = sut.copyIntoSuperclass(original);

        assertEquals(superExpected, superActual);
        assertEquals(SomeSuper.class, superActual.getClass());
    }

    @Test
    public void copyIntoSubclass() {
        expected = new SomeSub(I_RED, I_RED, S_RED, null);
        SomeClass original = new SomeClass(I_RED, I_RED, S_RED);
        actual = sut.copyIntoSubclass(original, SomeSub.class);

        assertEquals(expected, actual);
        assertEquals(SomeSub.class, actual.getClass());
    }

    static class SubjectCreatorTestValueProvider implements ValueProvider {

        public <T> Tuple<T> provide(TypeTag tag) {
            if (int.class.equals(tag.getType())) {
                return Tuple.of(I_RED, I_BLUE, I_RED);
            }
            if (String.class.equals(tag.getType())) {
                return Tuple.of(S_RED, S_BLUE, new String(S_RED));
            }
            throw new IllegalStateException();
        }
    }

    static class SomeSuper {

        final int x;

        public SomeSuper(int x) {
            this.x = x;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof SomeSuper;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SomeSuper)) {
                return false;
            }
            SomeSuper other = (SomeSuper) obj;
            return other.canEqual(this) && x == other.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x);
        }

        @Override
        public String toString() {
            return "SomeSuper: " + x;
        }
    }

    static class SomeClass extends SomeSuper {

        final int i;
        final String s;

        public SomeClass(int x, int i, String s) {
            super(x);
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof SomeClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SomeClass)) {
                return false;
            }
            SomeClass other = (SomeClass) obj;
            return (
                other.canEqual(this) &&
                super.equals(other) &&
                Objects.equals(i, other.i) &&
                Objects.equals(s, other.s)
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, i, s);
        }

        @Override
        public String toString() {
            return "SomeClass: " + x + "," + i + "," + s;
        }
    }

    static class SomeSub extends SomeClass {

        final String q;

        public SomeSub(int x, int i, String s, String q) {
            super(x, i, s);
            this.q = q;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof SomeSub;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SomeSub)) {
                return false;
            }
            SomeSub other = (SomeSub) obj;
            return other.canEqual(this) && super.equals(other) && Objects.equals(q, other.q);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, i, s, q);
        }

        @Override
        public String toString() {
            return "SomeSub: " + x + "," + i + "," + s + "," + q;
        }
    }
}
