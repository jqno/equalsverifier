package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.ArrayList;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.jupiter.api.Test;

public class JavaCollectionSubclassTest extends ExpectedExceptionTestBase {
    private static final String MESSAGE_FRAGMENT = "cannot verify subclasses of";

    @Test
    public void failWithHelpfulMessage_whenVerifyingArrayListSubclass() {
        expectFailure(MESSAGE_FRAGMENT, ArrayList.class.getCanonicalName());
        EqualsVerifier.forClass(CustomArrayList.class).verify();
    }

    private static final class CustomArrayList extends ArrayList<Integer> {
        private static final long serialVersionUID = 1L;
    }
}
