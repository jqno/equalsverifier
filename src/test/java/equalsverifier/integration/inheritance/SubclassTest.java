package equalsverifier.integration.inheritance;

import equalsverifier.EqualsVerifier;
import equalsverifier.utils.Warning;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.*;
import org.junit.Test;

import static equalsverifier.testhelpers.Util.defaultHashCode;

/**
 * Tests, among other things, the following approaches to inheritance with added
 * fields:
 *
 * 1. "blindly equals", as described by Tal Cohen in Dr. Dobb's Journal, May
 *    2002. See also http://www.ddj.com/java/184405053 and
 *    http://tal.forum2.org/equals
 *
 * 2. "can equal", as described by Odersky, Spoon and Venners in Programming in
 *    Scala.
 */
public class SubclassTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenClassIsFinal() {
        EqualsVerifier.forClass(FinalPoint.class)
                .verify();
    }

    @Test
    public void fail_whenClassIsNotEqualToATrivialSubclassWithEqualFields() {
        expectFailure("Subclass", "object is not equal to an instance of a trivial subclass with equal fields",
                "Maybe you forgot to add usingGetClass()", "consider making the class final.");
        EqualsVerifier.forClass(LiskovSubstitutionPrincipleBroken.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsOverridableAndBlindlyEqualsIsPresent() {
        expectFailure("Subclass", BlindlyEqualsPoint.class.getSimpleName(), "equals subclass instance",
                EqualSubclassForBlindlyEqualsPoint.class.getSimpleName());
        EqualsVerifier.forClass(BlindlyEqualsPoint.class)
                .withRedefinedSubclass(EqualSubclassForBlindlyEqualsPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsOverridableAndBlindlyEqualsIsPresent_givenACorrectSubclass() {
        EqualsVerifier.forClass(BlindlyEqualsPoint.class)
                .withRedefinedSubclass(BlindlyEqualsColorPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsOverriddenTwiceThroughBlindlyEquals_givenWithRedefinedSuperclass() {
        EqualsVerifier.forClass(BlindlyEqualsColorPoint.class)
                .withRedefinedSuperclass()
                .verify();
    }

    @Test
    public void fail_whenEqualsIsOverridableAndCanEqualIsPresent() {
        expectFailure("Subclass", CanEqualPoint.class.getSimpleName(), "equals subclass instance",
                EqualSubclassForCanEqualPoint.class.getSimpleName());
        EqualsVerifier.forClass(CanEqualPoint.class)
                .withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsOverridableAndCanEqualIsPresent_givenACorrectSubclass() {
        EqualsVerifier.forClass(CanEqualPoint.class)
                .withRedefinedSubclass(CanEqualColorPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsOverridenTwiceThroughCanEqual_givenWithRedefinedSuperclass() {
        EqualsVerifier.forClass(CanEqualColorPoint.class)
                .withRedefinedSuperclass()
                .verify();
    }

    @Test
    public void fail_whenWithRedefinedEqualsIsUsed_givenEqualsAndHashCodeAreFinal() {
        expectFailure("Subclass", FinalEqualsAndHashCode.class.getSimpleName(),
                "has a final equals method", "No need to supply a redefined subclass");
        EqualsVerifier.forClass(FinalEqualsAndHashCode.class)
                .withRedefinedSubclass(RedeFinalSubPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenClassIsAbstract_givenACorrectImplementationOfEqualsUnderInheritanceAndARedefinedSubclass() {
        EqualsVerifier.forClass(AbstractRedefinablePoint.class)
                .withRedefinedSubclass(SubclassForAbstractRedefinablePoint.class)
                .verify();
    }

    @Test
    public void fail_whenWithRedefinedSubclassIsUsed_givenStrictInheritanceWarningIsSuppressed() {
        expectFailure("withRedefinedSubclass", "weakInheritanceCheck", "are mutually exclusive");
        EqualsVerifier.forClass(CanEqualPoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
                .verify();
    }

    @Test
    public void fail_whenStrictInhertianceWarningIsSuppressed_givenWithRedefinedSubclassIsUsed() {
        expectFailure("withRedefinedSubclass", "weakInheritanceCheck", "are mutually exclusive");
        EqualsVerifier.forClass(CanEqualPoint.class)
                .withRedefinedSubclass(EqualSubclassForCanEqualPoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    static class LiskovSubstitutionPrincipleBroken {
        private final int x;

        public LiskovSubstitutionPrincipleBroken(int x) { this.x = x; }

        @Override
        public final boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return x == ((LiskovSubstitutionPrincipleBroken)obj).x;
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static class FinalEqualsAndHashCode {
        private final int x;
        private final int y;

        public FinalEqualsAndHashCode(int x, int y) { this.x = x; this.y = y; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof FinalEqualsAndHashCode)) {
                return false;
            }
            FinalEqualsAndHashCode p = (FinalEqualsAndHashCode)obj;
            return x == p.x && y == p.y;
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static final class RedeFinalSubPoint extends FinalEqualsAndHashCode {
        public RedeFinalSubPoint(int x, int y) {
            super(x, y);
        }
    }

    abstract static class AbstractRedefinablePoint {
        private final int x;
        private final int y;

        public AbstractRedefinablePoint(int x, int y) { this.x = x; this.y = y; }

        public boolean canEqual(Object obj) {
            return obj instanceof AbstractRedefinablePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractRedefinablePoint)) {
                return false;
            }
            AbstractRedefinablePoint p = (AbstractRedefinablePoint)obj;
            return p.canEqual(this) && p.x == x && p.y == y;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class SubclassForAbstractRedefinablePoint extends AbstractRedefinablePoint {
        private final Color color;

        public SubclassForAbstractRedefinablePoint(int x, int y, Color color) { super(x, y); this.color = color; }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof SubclassForAbstractRedefinablePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SubclassForAbstractRedefinablePoint)) {
                return false;
            }
            SubclassForAbstractRedefinablePoint p = (SubclassForAbstractRedefinablePoint)obj;
            return p.canEqual(this) && super.equals(obj) && color == p.color;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
