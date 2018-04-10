package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.Util;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class TransientFieldsTest extends IntegrationTestBase {
    @Test
    public void fail_whenTransientFieldsAreUsedInEquals() {
        expectFailure("Transient field", "should not be included in equals/hashCode contract");
        EqualsVerifier.forClass(TransientFields.class)
                .verify();
    }

    @Test
    public void succeed_whenTransientFieldsAreUsedInEquals_givenWarningsAreSuppressed() {
        EqualsVerifier.forClass(TransientFields.class)
                .suppress(Warning.TRANSIENT_FIELDS)
                .verify();
    }

    static final class TransientFields {
        private final int i;
        private final transient int j;

        public TransientFields(int i, int j) { this.i = i; this.j = j; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return Util.defaultHashCode(this); }
    }
}
