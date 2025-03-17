package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import org.junit.jupiter.api.Test;

class AssertTest {

    private static final Formatter FAIL = Formatter.of("fail");

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Assert.class);
    }

    @Test
    void assertEqualsObjectSuccess() {
        String red = new String("text");
        String blue = new String("text");
        Assert.assertEquals(FAIL, red, blue);
    }

    @Test
    void assertEqualsObjectFailure() {
        assertThatThrownBy(() -> Assert.assertEquals(FAIL, "one", "two"))
                .isInstanceOf(AssertionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("fail");
    }

    @Test
    void assertFalseSuccess() {
        Assert.assertFalse(FAIL, false);
    }

    @Test
    void assertFalseFailure() {
        assertThatThrownBy(() -> Assert.assertFalse(FAIL, true))
                .isInstanceOf(AssertionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("fail");
    }

    @Test
    void assertTrueSuccess() {
        Assert.assertTrue(FAIL, true);
    }

    @Test
    void assertTrueFailure() {
        assertThatThrownBy(() -> Assert.assertTrue(FAIL, false))
                .isInstanceOf(AssertionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("fail");
    }

    @Test
    void failFailure() {
        assertThatThrownBy(() -> Assert.fail(FAIL))
                .isInstanceOf(AssertionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("fail");
    }
}
