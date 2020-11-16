package nl.jqno.equalsverifier.integration.basic_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.jupiter.api.Test;

public class SymmetryTest extends ExpectedExceptionTestBase {
    private static final String SYMMETRY = "Symmetry";
    private static final String NOT_SYMMETRIC = "objects are not symmetric";
    private static final String AND = "and";

    @Test
    public void fail_whenEqualsIsNotSymmetrical() {
        expectFailure(
                SYMMETRY, NOT_SYMMETRIC, AND, SymmetryIntentionallyBroken.class.getSimpleName());
        EqualsVerifier.forClass(SymmetryIntentionallyBroken.class).verify();
    }

    static final class SymmetryIntentionallyBroken {
        private final int x;
        private final int y;

        public SymmetryIntentionallyBroken(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (goodEquals(obj)) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            return hashCode() > obj.hashCode();
        }

        public boolean goodEquals(Object obj) {
            if (!(obj instanceof SymmetryIntentionallyBroken)) {
                return false;
            }
            SymmetryIntentionallyBroken p = (SymmetryIntentionallyBroken) obj;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
