package equalsverifier.integration.operational;

import equalsverifier.EqualsVerifier;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.FinalPoint;
import org.junit.Test;

public class WithPrefabValuesTest extends ExpectedExceptionTestBase {
    private final FinalPoint red = new FinalPoint(1, 2);
    private final FinalPoint black = new FinalPoint(2, 3);

    @Test
    public void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest() {
        EqualsVerifier.forClass(FinalPoint.class)
                .withPrefabValues(FinalPoint.class, red, black)
                .verify();
    }

    @Test
    public void throw_whenTypeIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(null, red, black);
    }

    @Test
    public void throw_whenFirstPrefabValueIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, null, black);
    }

    @Test
    public void throw_whenSecondPrefabValueIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red, null);
    }

    @Test
    public void throw_whenThePrefabValuesAreTheSame() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Both values are equal.");

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red, red);
    }

    @Test
    public void throw_whenThePrefabValuesAreEqual() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Both values are equal.");

        FinalPoint red1 = new FinalPoint(4, 4);
        FinalPoint red2 = new FinalPoint(4, 4);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(FinalPoint.class, red1, red2);
    }
}
