package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.ArrayList;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class JavaCollectionSubclassTest {

    private static final String MESSAGE_FRAGMENT = "cannot verify subclasses of";

    @Test
    public void failWithHelpfulMessage_whenVerifyingArrayListSubclass() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(CustomArrayList.class).verify())
            .assertFailure()
            .assertMessageContains(MESSAGE_FRAGMENT, ArrayList.class.getCanonicalName());
    }

    private static final class CustomArrayList extends ArrayList<Integer> {

        private static final long serialVersionUID = 1L;
    }
}
