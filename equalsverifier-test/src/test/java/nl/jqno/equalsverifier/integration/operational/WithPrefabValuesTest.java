package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.PreconditionTypeHelper.StringPrecondition;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
class WithPrefabValuesTest {

    private final FinalPoint red = new FinalPoint(1, 2);
    private final FinalPoint blue = new FinalPoint(2, 3);
    private final FinalPoint redCopy = new FinalPoint(1, 2);

    @Test
    void succeed_userPrefabsComeBeforeBuiltinPrefabs_givenTwoValues() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Thrower.class).verify()).assertFailure();
        EqualsVerifier.forClass(Thrower.class).withPrefabValues(int.class, 42, 1337).verify();
    }

    @Test
    void succeed_userPrefabsComeBeforeBuiltinPrefabs_givenThreeValues() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Thrower.class).verify()).assertFailure();
        EqualsVerifier.forClass(Thrower.class).withPrefabValues(int.class, 42, 1337, 42).verify();
    }

    @Test
    void succeed_stringUserPrefabsComeBeforeBuiltinPrefabs_givenTwoValues() {
        ExpectedException.when(() -> EqualsVerifier.forClass(StringPrecondition.class).verify()).assertFailure();
        EqualsVerifier
                .forClass(StringPrecondition.class)
                .withPrefabValues(String.class, "precondition:x", "precondition:y")
                .verify();
    }

    @Test
    void succeed_stringUserPrefabsComeBeforeBuiltinPrefabs_givenThreeValues() {
        ExpectedException.when(() -> EqualsVerifier.forClass(StringPrecondition.class).verify()).assertFailure();
        EqualsVerifier
                .forClass(StringPrecondition.class)
                .withPrefabValues(String.class, "precondition:x", "precondition:y", "precondition:x")
                .verify();
    }

    @Test
    void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest_givenTwoValues() {
        EqualsVerifier.forClass(FinalPoint.class).withPrefabValues(FinalPoint.class, red, blue).verify();
    }

    @Test
    void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest_givenThreeValues() {
        EqualsVerifier.forClass(FinalPoint.class).withPrefabValues(FinalPoint.class, red, blue, redCopy).verify();
    }

    @Test
    void throw_whenTypeIsNull_givenTwoValues() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(WithPrefabValuesTest.class).withPrefabValues(null, red, blue))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenTypeIsNull_givenThreeValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(null, red, blue, redCopy))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenFirstPrefabValueIsNull_givenTwoValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, null, blue))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenFirstPrefabValueIsNull_givenThreeValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, null, blue, redCopy))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenSecondPrefabValueIsNull_givenTwoValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, null))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenSecondPrefabValueIsNull_givenThreeValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, null, redCopy))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenThirdPrefabValueIsNull_givenThreeValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, blue, null))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenThePrefabValuesAreTheSame_givenTwoValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, red))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
    }

    @Test
    void throw_whenThePrefabValuesAreTheSame_givenThreeValues() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, red, redCopy))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
    }

    @Test
    void throw_whenThePrefabValuesAreEqual_givenTwoValues() {
        FinalPoint red1 = new FinalPoint(4, 4);
        FinalPoint red2 = new FinalPoint(4, 4);

        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red1, red2))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
    }

    @Test
    void throw_whenThePrefabValuesAreEqual_givenThreeValues() {
        FinalPoint red1 = new FinalPoint(4, 4);
        FinalPoint red2 = new FinalPoint(4, 4);
        FinalPoint red3 = new FinalPoint(4, 4);

        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red1, red2, red3))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
    }

    @Test
    void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade_givenTwoValues() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .withPrefabValues(LocalDate.class, LocalDate.of(2018, 12, 24), LocalDate.of(2018, 12, 25))
                .verify();
    }

    @Test
    void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade_givenThreeValues() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .withPrefabValues(
                    LocalDate.class,
                    LocalDate.of(2018, 12, 24),
                    LocalDate.of(2018, 12, 25),
                    LocalDate.of(2018, 12, 24))
                .verify();
    }

    @Test
    void dontThrow_whenRedCopyIsSameInstanceAsRed_givenTheyArePrimitives() {
        EqualsVerifier.forClass(FinalPoint.class).withPrefabValues(int.class, 42, 1337, 42).verify();
    }

    @Test
    void throw_whenRedCopyIsSameInstanceAsRed_givenTheyAreReferences() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, blue, red))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "red and redCopy prefab values of type FinalPoint are the same object");
    }

    @Test
    void throw_whenRedCopyIsDifferentFromRed_givenTheyArePrimitives() {
        ExpectedException
                .when(
                    () -> EqualsVerifier.forClass(WithPrefabValuesTest.class).withPrefabValues(int.class, 42, 1337, -1))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "red and redCopy prefab values of type int should be equal but are not");
    }

    @Test
    void throw_whenRedCopyIsDifferentFromRed_givenTheyAreReferences() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, blue, new FinalPoint(42, 42)))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "red and redCopy prefab values of type FinalPoint should be equal but are not");
    }

    record Thrower(int i) {
        public Thrower {
            if (i == 1) {
                throw new IllegalStateException("i = " + i);
            }
        }
    }
}
