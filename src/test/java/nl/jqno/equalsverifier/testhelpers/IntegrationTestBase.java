package nl.jqno.equalsverifier.testhelpers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class IntegrationTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public void expectFailureWithCause(Class<? extends Throwable> cause, String... fragments) {
        thrown.expect(new CauseMatcher(cause));
        expectException(AssertionError.class, fragments);
    }

    public void expectFailure(String... fragments) {
        expectException(AssertionError.class, fragments);
    }

    public void expectException(Class<? extends Throwable> e, String... fragments) {
        thrown.expect(e);
        for (String messageFragment : fragments) {
            thrown.expectMessage(messageFragment);
        }
    }

    private static final class CauseMatcher extends BaseMatcher<Object> {
        private final Class<? extends Throwable> cause;

        public CauseMatcher(Class<? extends Throwable> cause) {
            this.cause = cause;
        }

        @Override
        public boolean matches(Object item) {
            Throwable actual = ((Throwable)item).getCause();
            if (cause == null) {
                return actual == null;
            }
            while (actual != null) {
                if (cause.isInstance(actual)) {
                    return true;
                }
                actual = actual.getCause();
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("unexpected exception");
        }
    }
}
