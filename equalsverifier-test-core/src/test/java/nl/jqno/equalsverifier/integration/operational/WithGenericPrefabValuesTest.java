package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainerContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainerContainer;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class WithGenericPrefabValuesTest {

    @Test
    public void sanityCheck() {
        EqualsVerifier
            .forClass(SingleGenericContainer.class)
            .withPrefabValues(
                SingleGenericContainer.class,
                new SingleGenericContainer<>(1),
                new SingleGenericContainer<>(2)
            )
            .verify();

        EqualsVerifier
            .forClass(DoubleGenericContainer.class)
            .withPrefabValues(
                DoubleGenericContainer.class,
                new DoubleGenericContainer<>(1, 1),
                new DoubleGenericContainer<>(2, 2)
            )
            .verify();
    }

    @Test
    public void fail_whenRegularPrefabValuesOfWrongTypeAreUsed_given1GenericParameter() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SingleGenericContainerContainer.class)
                    .withPrefabValues(
                        SingleGenericContainer.class,
                        new SingleGenericContainer<>(1),
                        new SingleGenericContainer<>(2)
                    )
                    .verify()
            )
            .assertFailure()
            .assertCause(ClassCastException.class)
            .assertMessageContains(
                "Generics",
                "for " + SingleGenericContainer.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenRegularPrefabValuesOfWrongTypeAreUsedAndMarkedAsNonnull_given1GenericParameter() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SingleGenericContainerContainer.class)
                    .withNonnullFields("string", "integer")
                    .withPrefabValues(
                        SingleGenericContainer.class,
                        new SingleGenericContainer<>(1),
                        new SingleGenericContainer<>(2)
                    )
                    .verify()
            )
            .assertFailure()
            .assertCause(ClassCastException.class)
            .assertMessageContains("Generics", "for the type that triggered the exception");
    }

    @Test
    public void succeed_whenPrefabValuesMatchGenericParameterInClassUnderTest_given1GenericParameter() {
        EqualsVerifier
            .forClass(SingleGenericContainerContainer.class)
            .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
            .verify();
    }

    @Test
    public void throw_whenTypeIsNull_given1GenericParameter() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SingleGenericContainerContainer.class)
                    .withGenericPrefabValues(null, SingleGenericContainer::new)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenFactoryIsNull_given1GenericParameter() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SingleGenericContainerContainer.class)
                    .withGenericPrefabValues(SingleGenericContainer.class, (Func1) null)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenFactoryHas2Parameters_given1GenericParameter() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SingleGenericContainerContainer.class)
                    .withGenericPrefabValues(
                        SingleGenericContainer.class,
                        (Func2) (a, b) -> new SingleGenericContainer<>(a)
                    )
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "number of generic type parameters doesn't match"
            );
    }

    @Test
    public void fail_whenRegularPrefabValuesOfWrongTypeAreUsed_given2GenericParameters() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DoubleGenericContainerContainer.class)
                    .withPrefabValues(
                        DoubleGenericContainer.class,
                        new DoubleGenericContainer<>(1, 1),
                        new DoubleGenericContainer<>(2, 2)
                    )
                    .verify()
            )
            .assertFailure()
            .assertCause(ClassCastException.class)
            .assertMessageContains(
                "Generics",
                "for " + DoubleGenericContainer.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenRegularPrefabValuesOfWrongTypeAreUsedAndMarkedAsNonnull_given2GenericParameters() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DoubleGenericContainerContainer.class)
                    .withNonnullFields("stringBoolean", "integerByte")
                    .withPrefabValues(
                        DoubleGenericContainer.class,
                        new DoubleGenericContainer<>(1, 1),
                        new DoubleGenericContainer<>(2, 2)
                    )
                    .verify()
            )
            .assertFailure()
            .assertCause(ClassCastException.class)
            .assertMessageContains("Generics", "for the type that triggered the exception");
    }

    @Test
    public void succeed_whenPrefabValuesMatchGenericParametersInClassUnderTest_given2GenericParameters() {
        EqualsVerifier
            .forClass(DoubleGenericContainerContainer.class)
            .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
            .verify();
    }

    @Test
    public void throw_whenTypeIsNull_given2GenericParameters() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DoubleGenericContainerContainer.class)
                    .withGenericPrefabValues(null, DoubleGenericContainer::new)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenFactoryIsNull_given2GenericParameters() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DoubleGenericContainerContainer.class)
                    .withGenericPrefabValues(DoubleGenericContainer.class, (Func2) null)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenFactoryHas1Parameter_given2GenericParameters() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DoubleGenericContainerContainer.class)
                    .withGenericPrefabValues(
                        DoubleGenericContainer.class,
                        (Func1) (a -> new DoubleGenericContainer<>(a, a))
                    )
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "number of generic type parameters doesn't match"
            );
    }
}
