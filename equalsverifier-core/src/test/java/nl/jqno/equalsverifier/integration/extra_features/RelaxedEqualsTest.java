package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Multiple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelaxedEqualsTest {

    private Multiple a;
    private Multiple b;
    private Multiple x;

    @BeforeEach
    public void setup() {
        a = new Multiple(1, 2);
        b = new Multiple(2, 1);
        x = new Multiple(2, 2);
    }

    @Test
    public void fail_whenObjectsWithDifferentFieldsAreEqual() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(Multiple.class).verify())
            .assertFailure()
            .assertMessageContains("Significant fields");
    }

    @Test
    public void succeed_whenObjectsWithDifferentFieldsAreEqual_givenTheyAreGivenAsRelaxedEqualExamples() {
        EqualsVerifier.forRelaxedEqualExamples(a, b).andUnequalExample(x).verify();
    }

    @Test
    public void fail_whenTheSameObjectIsGivenAsAnUnequalExample() {
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(a, b).andUnequalExamples(a))
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "an equal example also appears as unequal example."
            );
    }

    @Test
    public void succeed_whenAnUnusedFieldIsNull_givenItIsGivenAsARelaxedEqualExample() {
        EqualsVerifier
            .forRelaxedEqualExamples(
                new NullContainingSubMultiple(1, 2),
                new NullContainingSubMultiple(2, 1)
            )
            .andUnequalExample(new NullContainingSubMultiple(2, 2))
            .verify();
    }

    public class NullContainingSubMultiple extends Multiple {

        @SuppressWarnings("unused")
        private final String noValue = null;

        public NullContainingSubMultiple(int a, int b) {
            super(a, b);
        }
    }
}
