package nl.jqno.equalsverifier.testhelpers;

import static org.hamcrest.CoreMatchers.not;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class ExpectedExceptionTestBase {
    @Rule public ExpectedException thrown = ExpectedException.none();

    public void expectFailureWithCause(Class<? extends Throwable> cause, String... fragments) {
        thrown.expect(new CauseMatcher(cause));
        expectException(AssertionError.class, fragments);
    }

    public void expectFailureWithoutCause(Class<? extends Throwable> notCause) {
        thrown.expect(not(new CauseMatcher(notCause)));
        expectException(AssertionError.class);
    }

    public void expectFailure(String... fragments) {
        expectException(AssertionError.class, fragments);
    }

    public void expectDescription(String... fragments) {
        for (String descriptionFragment : fragments) {
            thrown.expect(new DescriptionMatcher(descriptionFragment));
        }
    }

    public void expectNotInDescription(String fragment) {
        thrown.expect(not(new DescriptionMatcher(fragment)));
    }

    public void expectException(Class<? extends Throwable> e, String... fragments) {
        thrown.expect(e);
        for (String messageFragment : fragments) {
            thrown.expectMessage(messageFragment);
        }
    }

    public void expectCauseMessageDoesNotContain(String doesNotContain) {
        thrown.expect(not(new CauseDescriptionMatcher(doesNotContain)));
    }

    private static final class CauseMatcher extends BaseMatcher<Object> {
        private final Class<? extends Throwable> cause;

        public CauseMatcher(Class<? extends Throwable> cause) {
            this.cause = cause;
        }

        @Override
        public boolean matches(Object item) {
            Throwable actual = ((Throwable) item).getCause();
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

    private static final class DescriptionMatcher extends BaseMatcher<Object> {
        private final String description;

        public DescriptionMatcher(String description) {
            this.description = description;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof MessagingException)) {
                return false;
            }
            MessagingException me = (MessagingException) item;
            if (me.getDescription() == null) {
                return false;
            }
            return me.getDescription().contains(description);
        }

        @Override
        public void describeTo(Description dsc) {
            dsc.appendText("description [" + description + "]");
        }
    }

    public static final class CauseDescriptionMatcher extends BaseMatcher<Object> {
        private final String description;

        public CauseDescriptionMatcher(String description) {
            this.description = description;
        }

        @Override
        public boolean matches(Object item) {
            Throwable cause = ((Throwable) item).getCause();
            while (cause != null) {
                if (cause.getMessage() != null && cause.getMessage().contains(description)) {
                    return true;
                }
                cause = cause.getCause();
            }
            return false;
        }

        @Override
        public void describeTo(Description dsc) {
            dsc.appendText("cause contains [" + description + "]");
        }
    }
}
