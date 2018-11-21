package equalsverifier.integration.extra_features;

import equalsverifier.EqualsVerifier;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.Multiple;
import org.junit.Before;
import org.junit.Test;

public class RelaxedEqualsTest extends ExpectedExceptionTestBase {
    private Multiple a;
    private Multiple b;
    private Multiple x;

    @Before
    public void setup() {
        a = new Multiple(1, 2);
        b = new Multiple(2, 1);
        x = new Multiple(2, 2);
    }

    @Test
    public void fail_whenObjectsWithDifferentFieldsAreEqual() {
        expectFailure("Significant fields");
        EqualsVerifier.forClass(Multiple.class)
                .verify();
    }

    @Test
    public void succeed_whenObjectsWithDifferentFieldsAreEqual_givenTheyAreGivenAsRelaxedEqualExamples() {
        EqualsVerifier.forRelaxedEqualExamples(a, b)
                .andUnequalExample(x)
                .verify();
    }

    @Test
    public void fail_whenTheSameObjectIsGivenAsAnUnequalExample() {
        expectException(IllegalArgumentException.class, "An equal example also appears as unequal example.");
        EqualsVerifier.forRelaxedEqualExamples(a, b)
                .andUnequalExamples(a);
    }

    @Test
    public void succeed_whenAnUnusedFieldIsNull_givenItIsGivenAsARelaxedEqualExample() {
        EqualsVerifier.forRelaxedEqualExamples(new NullContainingSubMultiple(1, 2), new NullContainingSubMultiple(2, 1))
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
