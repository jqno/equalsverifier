package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class WithPrefabValuesTest extends ExpectedExceptionTestBase {
    private final FinalPoint red = new FinalPoint(1, 2);
    private final FinalPoint blue = new FinalPoint(2, 3);

    @Test
    public void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest() {
        EqualsVerifier.forClass(FinalPoint.class)
                .withPrefabValues(FinalPoint.class, red, blue)
                .verify();
    }

    @Test
    public void throw_whenTypeIsNull() {
        expectException(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class).withPrefabValues(null, red, blue);
    }

    @Test
    public void throw_whenFirstPrefabValueIsNull() {
        expectException(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, null, blue);
    }

    @Test
    public void throw_whenSecondPrefabValueIsNull() {
        expectException(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red, null);
    }

    @Test
    public void throw_whenThePrefabValuesAreTheSame() {
        expectException(IllegalStateException.class, "Precondition", "both values are equal");

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red, red);
    }

    @Test
    public void throw_whenThePrefabValuesAreEqual() {
        expectException(IllegalStateException.class, "Precondition", "both values are equal");

        FinalPoint red1 = new FinalPoint(4, 4);
        FinalPoint red2 = new FinalPoint(4, 4);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red1, red2);
    }

    @Test
    public void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade() {
        EqualsVerifier.forClass(FinalPoint.class)
                .withPrefabValues(
                        LocalDate.class, LocalDate.of(2018, 12, 24), LocalDate.of(2018, 12, 25))
                .verify();
    }
}
