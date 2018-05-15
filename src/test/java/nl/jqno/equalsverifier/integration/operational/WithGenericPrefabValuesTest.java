package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@SuppressWarnings("unused")
public class WithGenericPrefabValuesTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void sanityCheck() {
        EqualsVerifier.forClass(SingleGenericContainer.class)
            .withPrefabValues(SingleGenericContainer.class, new SingleGenericContainer<>(1), new SingleGenericContainer<>(2))
            .verify();
    }

    @Test
    public void throw_whenRegularPrefabValuesOfWrongTypeAreUsed() {
        thrown.expectCause(instanceOf(ClassCastException.class));

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withPrefabValues(SingleGenericContainer.class, new SingleGenericContainer<>(1), new SingleGenericContainer<>(2))
                .verify();
    }

    @Test
    public void succeed_whenPrefabValuesMatchOnlyGenericParameterInClassUnderTest() {
        EqualsVerifier.forClass(SingleGenericContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();
    }

    @Test
    public void throw_whenTypeIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(null, SingleGenericContainer::new);
    }

    @Test
    public void throw_whenFactoryIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
              .withGenericPrefabValues(SingleGenericContainer.class, null);
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
}
