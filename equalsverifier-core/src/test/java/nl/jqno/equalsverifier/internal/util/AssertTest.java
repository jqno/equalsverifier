package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class AssertTest {

    private static final Formatter FAIL = Formatter.of("fail");

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Assert.class);
    }

    @Test
    public void assertEqualsObjectSuccess() {
        String red = new String("text");
        String blue = new String("text");
        Assert.assertEquals(FAIL, red, blue);
    }

    @Test
    public void assertEqualsObjectFailure() {
        ExpectedException
            .when(() -> Assert.assertEquals(FAIL, "one", "two"))
            .assertThrows(AssertionException.class)
            .assertDescriptionContains("fail");
    }

    @Test
    public void assertFalseSuccess() {
        Assert.assertFalse(FAIL, false);
    }

    @Test
    public void assertFalseFailure() {
        ExpectedException
            .when(() -> Assert.assertFalse(FAIL, true))
            .assertThrows(AssertionException.class)
            .assertDescriptionContains("fail");
    }

    @Test
    public void assertTrueSuccess() {
        Assert.assertTrue(FAIL, true);
    }

    @Test
    public void assertTrueFailure() {
        ExpectedException
            .when(() -> Assert.assertTrue(FAIL, false))
            .assertThrows(AssertionException.class)
            .assertDescriptionContains("fail");
    }

    @Test
    public void failFailure() {
        ExpectedException
            .when(() -> Assert.fail(FAIL))
            .assertThrows(AssertionException.class)
            .assertDescriptionContains("fail");
    }
}
