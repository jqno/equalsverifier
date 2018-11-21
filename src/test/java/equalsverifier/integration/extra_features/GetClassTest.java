package equalsverifier.integration.extra_features;

import equalsverifier.EqualsVerifier;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.Color;
import equalsverifier.testhelpers.types.FinalMethodsPoint;
import equalsverifier.testhelpers.types.GetClassPoint;
import equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import static equalsverifier.testhelpers.Util.defaultHashCode;

public class GetClassTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenEqualsUsesGetClassInsteadOfInstanceof_givenUsingGetClassIsUsed() {
        EqualsVerifier.forClass(GetClassPoint.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void fail_whenEqualsUsesGetClassButForgetsToCheckNull_givenUsingGetClassIsUsed() {
        expectFailureWithCause(NullPointerException.class, "Non-nullity: NullPointerException thrown");
        EqualsVerifier.forClass(GetClassPointNull.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void fail_whenEqualsUsesInstanceof_givenUsingGetClassIsUsed() {
        expectFailure("Subclass", "object is equal to an instance of a trivial subclass with equal fields",
                "This should not happen when using getClass().");
        EqualsVerifier.forClass(FinalMethodsPoint.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void succeed_whenSuperclassUsesGetClass_givenUsingGetClassIsUsed() {
        EqualsVerifier.forClass(GetClassColorPoint.class)
                .usingGetClass()
                .verify();
    }

    @Test
    public void fail_whenEqualsUsesGetClassButSuperclassUsesInstanceof_givenUsingGetClassIsUsed() {
        expectFailure("Redefined superclass", GetClassColorPointWithEqualSuper.class.getSimpleName(),
                "should not equal superclass instance", Point.class.getSimpleName(), "but it does");
        EqualsVerifier.forClass(GetClassColorPointWithEqualSuper.class)
                .usingGetClass()
                .verify();
    }

    static class GetClassPointNull {
        private final int x;
        private final int y;

        public GetClassPointNull(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != getClass()) {
                return false;
            }
            GetClassPointNull p = (GetClassPointNull)obj;
            return p.x == x && p.y == y;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class GetClassColorPoint extends GetClassPoint {
        private final Color color;

        public GetClassColorPoint(int x, int y, Color color) { super(x, y); this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                GetClassColorPoint other = (GetClassColorPoint)obj;
                return color == other.color;
            }
            return false;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class GetClassColorPointWithEqualSuper extends Point {
        private final Color color;

        public GetClassColorPointWithEqualSuper(int x, int y, Color color) { super(x, y); this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            GetClassColorPointWithEqualSuper p = (GetClassColorPointWithEqualSuper)obj;
            return super.equals(obj) && color == p.color;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
