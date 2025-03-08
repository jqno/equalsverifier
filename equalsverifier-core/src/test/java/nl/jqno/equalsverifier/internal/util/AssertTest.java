package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
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
        ExpectedException
                .when(() -> Assert.assertEquals(FAIL, "one", "two"))
                .assertThrows(AssertionException.class)
                .assertDescriptionContains("fail");
    }

    @Test
    void assertFalseSuccess() {
        Assert.assertFalse(FAIL, false);
    }

    @Test
    void assertFalseFailure() {
        ExpectedException
                .when(() -> Assert.assertFalse(FAIL, true))
                .assertThrows(AssertionException.class)
                .assertDescriptionContains("fail");
    }

    @Test
    void assertTrueSuccess() {
        Assert.assertTrue(FAIL, true);
    }

    @Test
    void assertTrueFailure() {
        ExpectedException
                .when(() -> Assert.assertTrue(FAIL, false))
                .assertThrows(AssertionException.class)
                .assertDescriptionContains("fail");
    }

    @Test
    void failFailure() {
        ExpectedException
                .when(() -> Assert.fail(FAIL))
                .assertThrows(AssertionException.class)
                .assertDescriptionContains("fail");
    }
}
