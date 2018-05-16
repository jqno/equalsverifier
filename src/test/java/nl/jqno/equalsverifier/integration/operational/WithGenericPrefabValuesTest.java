package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@SuppressWarnings({"unused", "unchecked"})
public class WithGenericPrefabValuesTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void sanityCheck() {
        EqualsVerifier.forClass(SingleGenericContainer.class)
                .withPrefabValues(SingleGenericContainer.class, new SingleGenericContainer<>(1), new SingleGenericContainer<>(2))
                .verify();

        EqualsVerifier.forClass(DoubleGenericContainer.class)
                .withPrefabValues(DoubleGenericContainer.class, new DoubleGenericContainer<>(1, 1), new DoubleGenericContainer<>(2, 2))
                .verify();
    }

    @Test
    public void throw_whenRegularPrefabValuesOfWrongTypeAreUsed_given1GenericParameter() {
        thrown.expectCause(instanceOf(ClassCastException.class));

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withPrefabValues(SingleGenericContainer.class, new SingleGenericContainer<>(1), new SingleGenericContainer<>(2))
                .verify();
    }

    @Test
    public void succeed_whenPrefabValuesMatchGenericParameterInClassUnderTest_given1GenericParameter() {
        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();
    }

    @Test
    public void throw_whenTypeIsNull_given1GenericParameter() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(null, SingleGenericContainer::new);
    }

    @Test
    public void throw_whenFactoryIsNull_given1GenericParameter() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, (Func1)null);
    }

    @Test
    public void throw_whenFactoryHas2Parameters_given1GenericParameter() {
        thrown.expect(IllegalArgumentException.class);

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
            .withGenericPrefabValues(SingleGenericContainer.class, (Func2)(a, b) -> new SingleGenericContainer<>(a));
    }

    @Test
    public void throw_whenRegularPrefabValuesOfWrongTypeAreUsed_given2GenericParameters() {
        thrown.expectCause(instanceOf(ClassCastException.class));

        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                .withPrefabValues(DoubleGenericContainer.class, new DoubleGenericContainer<>(1, 1), new DoubleGenericContainer<>(2, 2))
                .verify();
    }

    @Test
    public void succeed_whenPrefabValuesMatchGenericParametersInClassUnderTest_given2GenericParameters() {
        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
                .verify();
    }

    @Test
    public void throw_whenTypeIsNull_given2GenericParameters() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                .withGenericPrefabValues(null, DoubleGenericContainer::new);
    }

    @Test
    public void throw_whenFactoryIsNull_given2GenericParameters() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                .withGenericPrefabValues(DoubleGenericContainer.class, (Func2)null);
    }

    @Test
    public void throw_whenFactoryHas1Parameter_given2GenericParameters() {
        thrown.expect(IllegalArgumentException.class);

        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
            .withGenericPrefabValues(DoubleGenericContainer.class, (Func1)(a -> new DoubleGenericContainer<>(a, a)));
    }

    private static final class SingleGenericContainerContainer {
        private final SingleGenericContainer<String> string;
        private final SingleGenericContainer<Integer> integer;

        private SingleGenericContainerContainer(SingleGenericContainer<String> string, SingleGenericContainer<Integer> integer) {
            this.string = string;
            this.integer = integer;
        }

        @Override public boolean equals(Object obj) {
            if (string != null) {
                String s = string.t;
            }
            return defaultEquals(this, obj);
        }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    private static final class SingleGenericContainer<T> {
        private final SingleGenericContainer<Void> justToMakeItRecursiveAndForcePrefabValues = null;

        private final T t;

        private SingleGenericContainer(T t) { this.t = t; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    private static final class DoubleGenericContainerContainer {
        private final DoubleGenericContainer<String, Boolean> stringBoolean;
        private final DoubleGenericContainer<Integer, Byte> integerByte;

        private DoubleGenericContainerContainer(
                DoubleGenericContainer<String, Boolean> stringBoolean,
                DoubleGenericContainer<Integer, Byte> integerByte) {
            this.stringBoolean = stringBoolean;
            this.integerByte = integerByte;
        }

        @Override public boolean equals(Object obj) {
            if (stringBoolean != null) {
                String s = stringBoolean.t;
            }
            return defaultEquals(this, obj);
        }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    private static final class DoubleGenericContainer<T, U> {
        private final DoubleGenericContainer<Void, Void> justToMakeItRecursiveAndForcePrefabValues = null;

        private final T t;
        private final U u;

        private DoubleGenericContainer(T t, U u) { this.t = t; this.u = u; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
