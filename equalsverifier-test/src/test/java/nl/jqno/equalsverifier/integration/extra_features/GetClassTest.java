package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Color;
import nl.jqno.equalsverifier_testhelpers.types.FinalMethodsPoint;
import nl.jqno.equalsverifier_testhelpers.types.GetClassPoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class GetClassTest {

    @Test
    void fail_whenEqualsUsesGetClassInsteadOfInstanceof() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassPoint.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Subclass",
                    "object is not equal to an instance of a trivial subclass with equal fields",
                    "Maybe you forgot to add usingGetClass()");
    }

    @Test
    void succeed_whenEqualsUsesGetClassInsteadOfInstanceof_givenUsingGetClassIsUsed() {
        EqualsVerifier.forClass(GetClassPoint.class).usingGetClass().verify();
    }

    @Test
    void succeed_whenEqualsUsesGetClassInsteadOfInstanceof_givenWarningStrictInheritanceIsSuppressed() {
        EqualsVerifier.forClass(GetClassPoint.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }

    @Test
    void fail_whenEqualsUsesGetClassButForgetsToCheckNull_givenUsingGetClassIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassPointNull.class).usingGetClass().verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains("Non-nullity: NullPointerException thrown");
    }

    @Test
    void fail_whenEqualsUsesInstanceof_givenUsingGetClassIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalMethodsPoint.class).usingGetClass().verify())
                .assertFailure()
                .assertMessageContains(
                    "Subclass",
                    "object is equal to an instance of a trivial subclass with equal fields",
                    "This should not happen when using getClass().");
    }

    @Test
    void fail_whenSuperclassUsesGetClass() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassColorPoint.class).verify())
                .assertFailure()
                .assertMessageContains("Symmetry", "does not equal superclass instance");
    }

    @Test
    void succeed_whenSuperclassUsesGetClass_givenUsingGetClassIsUsed() {
        EqualsVerifier.forClass(GetClassColorPoint.class).usingGetClass().verify();
    }

    @Test
    void succeed_whenSuperclassUsesGetClass_givenWarningStrictInheritanceIsSuppressed() {
        EqualsVerifier.forClass(GetClassColorPoint.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }

    @Test
    void fail_whenEqualsUsesGetClassButSuperclassUsesInstanceof_givenUsingGetClassIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassColorPointWithEqualSuper.class).usingGetClass().verify())
                .assertFailure()
                .assertMessageContains(
                    "Redefined superclass",
                    GetClassColorPointWithEqualSuper.class.getSimpleName(),
                    "should not equal superclass instance",
                    Point.class.getSimpleName(),
                    "but it does");
    }

    static class GetClassPointNull {

        private final int x;
        private final int y;

        public GetClassPointNull(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != getClass()) {
                return false;
            }
            GetClassPointNull p = (GetClassPointNull) obj;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static class GetClassColorPoint extends GetClassPoint {

        private final Color color;

        public GetClassColorPoint(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                GetClassColorPoint other = (GetClassColorPoint) obj;
                return color == other.color;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 53 * super.hashCode() + Objects.hash(color);
        }
    }

    static class GetClassColorPointWithEqualSuper extends Point {

        private final Color color;

        public GetClassColorPointWithEqualSuper(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            GetClassColorPointWithEqualSuper p = (GetClassColorPointWithEqualSuper) obj;
            return super.equals(obj) && color == p.color;
        }

        @Override
        public int hashCode() {
            return 53 * super.hashCode() + Objects.hash(color);
        }
    }
}
