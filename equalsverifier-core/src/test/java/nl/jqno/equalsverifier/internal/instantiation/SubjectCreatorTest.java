package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class SubjectCreatorTest {

    private static final int I_RED = 42;
    private static final int I_BLUE = 1337;
    private static final String S_RED = "abc";
    private static final String S_BLUE = "xyz";

    private final Configuration<SomeClass> config = ConfigurationHelper.emptyConfiguration(SomeClass.class);
    private final ValueProvider valueProvider = new SubjectCreatorTestValueProvider();
    private final Objenesis objenesis = new ObjenesisStd();
    private SubjectCreator<SomeClass> sut = new SubjectCreator<>(config, valueProvider, objenesis);

    private Field fieldX;
    private Field fieldI;
    private Field fieldS;

    private SomeClass expected;
    private SomeClass actual;

    @BeforeEach
    void setup() throws NoSuchFieldException {
        fieldX = SomeSuper.class.getDeclaredField("x");
        fieldI = SomeClass.class.getDeclaredField("i");
        fieldS = SomeClass.class.getDeclaredField("s");
    }

    @Test
    void plain() {
        expected = new SomeClass(I_RED, I_RED, S_RED);
        actual = sut.plain();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void plain_abstract() {
        var anotherConfig = ConfigurationHelper.emptyConfiguration(Abstract.class);
        var anotherSut = new SubjectCreator<>(anotherConfig, valueProvider, objenesis);
        var anotherActual = anotherSut.plain();

        assertThat(anotherActual.s).isEqualTo(S_RED);
    }

    @Test
    void plain_sealedAbstract() {
        var anotherConfig = ConfigurationHelper.emptyConfiguration(SealedAbstract.class);
        var anotherSut = new SubjectCreator<>(anotherConfig, valueProvider, objenesis);
        var anotherActual = anotherSut.plain();

        assertThat(anotherActual.s).isEqualTo(S_RED);

        // SubjectCreator returns a subclass with new fields!
        assertThat(((SealedSub) anotherActual).i).isEqualTo(I_RED);
    }

    @Test
    void withFieldDefaulted_super() {
        expected = new SomeClass(0, I_RED, S_RED);
        actual = sut.withFieldDefaulted(fieldX);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldDefaulted_primitive() {
        expected = new SomeClass(I_RED, 0, S_RED);
        actual = sut.withFieldDefaulted(fieldI);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldDefaulted_object() {
        expected = new SomeClass(I_RED, I_RED, null);
        actual = sut.withFieldDefaulted(fieldS);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withAllFieldsDefaulted() {
        expected = new SomeClass(0, 0, null);
        actual = sut.withAllFieldsDefaulted();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withAllMatchingFieldsDefaulted() {
        expected = new SomeClass(I_RED, 0, null);
        actual = sut.withAllMatchingFieldsDefaulted(f -> !f.getName().equals("x"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withAllFieldsDefaultedExcept() {
        expected = new SomeClass(0, I_RED, null);
        actual = sut.withAllFieldsDefaultedExcept(fieldI);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldSetTo_super() {
        expected = new SomeClass(99, I_RED, S_RED);
        actual = sut.withFieldSetTo(fieldX, 99);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldSetTo_primitive() {
        expected = new SomeClass(I_RED, 99, S_RED);
        actual = sut.withFieldSetTo(fieldI, 99);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldSetTo_object() {
        expected = new SomeClass(I_RED, I_RED, "value");
        actual = sut.withFieldSetTo(fieldS, "value");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldChanged_super() {
        expected = new SomeClass(I_BLUE, I_RED, S_RED);
        actual = sut.withFieldChanged(fieldX);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldChanged_primitive() {
        expected = new SomeClass(I_RED, I_BLUE, S_RED);
        actual = sut.withFieldChanged(fieldI);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withFieldChanged_object() {
        expected = new SomeClass(I_RED, I_RED, S_BLUE);
        actual = sut.withFieldChanged(fieldS);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withAllFieldsChanged() {
        expected = new SomeClass(I_BLUE, I_BLUE, S_BLUE);
        actual = sut.withAllFieldsChanged();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withAllFieldsShallowlyChanged() {
        expected = new SomeClass(I_RED, I_BLUE, S_BLUE);
        actual = sut.withAllFieldsShallowlyChanged();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void copyIntoSuperclass() {
        SomeSuper superExpected = new SomeSuper(I_RED);
        SomeClass original = new SomeClass(I_RED, I_RED, S_RED);
        Object superActual = sut.copyIntoSuperclass(original);

        assertThat(superActual).isEqualTo(superExpected);
        assertThat(superActual.getClass()).isEqualTo(SomeSuper.class);
    }

    @Test
    void copyIntoSubclass() {
        expected = new SomeSub(I_RED, I_RED, S_RED, null);
        SomeClass original = new SomeClass(I_RED, I_RED, S_RED);
        actual = sut.copyIntoSubclass(original, SomeSub.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.getClass()).isEqualTo(SomeSub.class);
    }

    @Test
    void noValueFound() {
        sut = new SubjectCreator<>(config, new NoValueProvider(), objenesis);

        assertThatThrownBy(() -> sut.plain())
                .isInstanceOf(NoValueException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("int");

        assertThat(actual).isEqualTo(expected);
    }

    static class SubjectCreatorTestValueProvider implements ValueProvider {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            if (int.class.equals(tag.getType())) {
                return Optional.of((Tuple<T>) new Tuple<>(I_RED, I_BLUE, I_RED));
            }
            if (String.class.equals(tag.getType())) {
                return Optional.of((Tuple<T>) new Tuple<>(S_RED, S_BLUE, new String(S_RED)));
            }
            if (SealedSub.class.equals(tag.getType())) {
                var red = new SealedSub("1", 1);
                return Optional.of((Tuple<T>) new Tuple<>(red, new SealedSub("2", 2), red));
            }
            return Optional.empty();
        }
    }

    static class NoValueProvider implements ValueProvider {

        @Override
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            return Optional.empty();
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
            return other.canEqual(this) && super.equals(other) && i == other.i && Objects.equals(s, other.s);
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

    static abstract class Abstract {
        private final String s;

        public Abstract(String s) {
            this.s = s;
        }
    }

    sealed static abstract class SealedAbstract permits SealedSub {
        private final String s;

        public SealedAbstract(String s) {
            this.s = s;
        }
    }

    static final class SealedSub extends SealedAbstract {
        private final int i;

        public SealedSub(String s, int i) {
            super(s);
            this.i = i;
        }
    }
}
