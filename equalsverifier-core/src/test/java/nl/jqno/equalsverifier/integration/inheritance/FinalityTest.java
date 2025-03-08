package nl.jqno.equalsverifier.integration.inheritance;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class FinalityTest {

    private static final String BOTH_FINAL_OR_NONFINAL =
            "Finality: equals and hashCode must both be final or both be non-final";
    private static final String SUBCLASS = "Subclass";
    private static final String SUPPLY_AN_INSTANCE =
            "Make your class or your %s method final, or supply an instance of a redefined subclass using withRedefinedSubclass";

    @Test
    void fail_whenEqualsIsFinalButHashCodeIsNonFinal() {
        check(FinalEqualsNonFinalHashCode.class);
    }

    @Test
    void fail_whenEqualsIsNotFinal_givenAClassThatIsNotFinal() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(Point.class).verify())
                .assertFailure()
                .assertMessageContains(
                    SUBCLASS,
                    "equals is not final",
                    SUPPLY_AN_INSTANCE.formatted("equals"),
                    "if equals cannot be final");
    }

    @Test
    void succeed_whenEqualsIsFinalButHashCodeIsNonFinal_givenWarningIsSuppressed() {
        EqualsVerifier
                .forClass(FinalEqualsNonFinalHashCode.class)
                .usingGetClass()
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    void succeed_whenEqualsIsNotFinal_givenAClassThatIsNotFinalAndWarningIsSuppressed() {
        EqualsVerifier.forClass(Point.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }

    @Test
    void fail_whenEqualsIsNonFinalButHashCodeIsFinal() {
        check(NonFinalEqualsFinalHashCode.class);
    }

    @Test
    void fail_whenHashCodeIsNotFinal_givenAClassThatIsNotFinalAndAnEqualsMethodThatIsFinal() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalEqualsPoint.class).verify())
                .assertFailure()
                .assertMessageContains(
                    SUBCLASS,
                    "hashCode is not final",
                    SUPPLY_AN_INSTANCE.formatted("hashCode"),
                    "if hashCode cannot be final");
    }

    @Test
    void succeed_whenEqualsIsNonFinalButHashCodeIsFinal_givenWarningsAreSuppressed() {
        EqualsVerifier
                .forClass(NonFinalEqualsFinalHashCode.class)
                .usingGetClass()
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    void succeed_whenHashCodeIsNotFinal_givenAClassThatIsNotFinalAndAnEqualsMethodThatIsFinalAndWarningIsSuppressed() {
        EqualsVerifier.forClass(FinalEqualsPoint.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }

    private <T> void check(Class<T> type) {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(type).usingGetClass().verify())
                .assertFailure()
                .assertMessageContains(BOTH_FINAL_OR_NONFINAL);
    }

    static class FinalEqualsNonFinalHashCode {

        private final int i;

        public FinalEqualsNonFinalHashCode(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            FinalEqualsNonFinalHashCode other = (FinalEqualsNonFinalHashCode) obj;
            return other.i == i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static class NonFinalEqualsFinalHashCode {

        private final int i;

        public NonFinalEqualsFinalHashCode(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            NonFinalEqualsFinalHashCode other = (NonFinalEqualsFinalHashCode) obj;
            return other.i == i;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
        }
    }

    static class FinalEqualsPoint extends Point {

        public FinalEqualsPoint(int x, int y) {
            super(x, y);
        }

        @Override
        public final boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
