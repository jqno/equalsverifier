package equalsverifier.integration.basic_contract;

import equalsverifier.EqualsVerifier;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.Point;
import org.junit.Test;

public class NonNullityTest extends ExpectedExceptionTestBase {
    @Test
    public void fail_whenNullPointerExceptionIsThrown_givenNullInput() {
        expectFailure("Non-nullity: NullPointerException thrown");
        EqualsVerifier.forClass(NullPointerExceptionThrower.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsReturnsTrue_givenNullInput() {
        expectFailure("Non-nullity: true returned for null value");
        EqualsVerifier.forClass(NullReturnsTrue.class)
                .verify();
    }

    static final class NullPointerExceptionThrower extends Point {
        public NullPointerExceptionThrower(int x, int y) { super(x, y); }

        @Override
        public boolean equals(Object obj) {
            if (!obj.getClass().equals(getClass())) {
                return false;
            }
            return super.equals(obj);
        }
    }

    static final class NullReturnsTrue extends Point {
        public NullReturnsTrue(int x, int y) { super(x, y); }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return true;
            }
            return super.equals(obj);
        }
    }
}
