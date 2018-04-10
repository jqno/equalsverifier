package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

public class DontOverrideEqualsTest extends IntegrationTestBase {
    @Test
    public void fail_whenEqualsIsInheritedDirectlyFromObject() {
        expectFailure("Equals is inherited directly from Object");
        EqualsVerifier.forClass(NoEqualsNoHashCodeMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsInheritedDirectlyFromObject_givenDirectlyInheritedWarningIsSuppressed() {
        EqualsVerifier.forClass(NoEqualsNoHashCodeMethod.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsInheritedButNotFromObject() {
        EqualsVerifier.forClass(InheritedEqualsAndHashCodeMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenClassIsAPojoAndEqualsIsInheritedDirectlyFromObject_givenVariousWarningsAreSuppressed() {
        EqualsVerifier.forClass(Pojo.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .suppress(Warning.NONFINAL_FIELDS, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    static final class NoEqualsNoHashCodeMethod {}

    static final class InheritedEqualsAndHashCodeMethod extends Point {
        InheritedEqualsAndHashCodeMethod(int x, int y) { super(x, y); }
    }

    public final class Pojo {
        private String s;

        public void setS(String value) {
            this.s = value;
        }

        public String getS() {
            return s;
        }

        @Override
        public String toString() {
            return getClass().getName() + " " + s;
        }
    }
}
