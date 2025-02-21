package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

class WithPrefabValuesTest {

    private final FinalPoint red = new FinalPoint(1, 2);
    private final FinalPoint blue = new FinalPoint(2, 3);

    @Test
    void succeed_userPrefabsComeBeforeBuiltinPrefabs() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Thrower.class).verify()).assertFailure();
        EqualsVerifier.forClass(Thrower.class).withPrefabValues(int.class, 42, 1337).verify();
    }

    @Test
    void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest() {
        EqualsVerifier.forClass(FinalPoint.class).withPrefabValues(FinalPoint.class, red, blue).verify();
    }

    @Test
    void throw_whenTypeIsNull() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(WithPrefabValuesTest.class).withPrefabValues(null, red, blue))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenFirstPrefabValueIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, null, blue))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenSecondPrefabValueIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, null))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenThePrefabValuesAreTheSame() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(WithPrefabValuesTest.class)
                            .withPrefabValues(FinalPoint.class, red, red))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
    }

    @Test
    void throw_whenThePrefabValuesAreEqual() {
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
    void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .withPrefabValues(LocalDate.class, LocalDate.of(2018, 12, 24), LocalDate.of(2018, 12, 25))
                .verify();
    }

    record Thrower(int i) {
        public Thrower {
            if (i == 1) {
                throw new IllegalStateException("i = " + i);
            }
        }
    }
}
