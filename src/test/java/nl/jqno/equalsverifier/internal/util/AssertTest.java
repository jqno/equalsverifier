package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.jupiter.api.Test;

public class AssertTest extends ExpectedExceptionTestBase {
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
        expectException(AssertionException.class);
        expectDescription("fail");
        Assert.assertEquals(FAIL, "one", "two");
    }

    @Test
    public void assertFalseSuccess() {
        Assert.assertFalse(FAIL, false);
    }

    @Test
    public void assertFalseFailure() {
        expectException(AssertionException.class);
        expectDescription("fail");
        Assert.assertFalse(FAIL, true);
    }

    @Test
    public void assertTrueSuccess() {
        Assert.assertTrue(FAIL, true);
    }

    @Test
    public void assertTrueFailure() {
        expectException(AssertionException.class);
        expectDescription("fail");
        Assert.assertTrue(FAIL, false);
    }

    @Test
    public void failFailure() {
        expectException(AssertionException.class);
        expectDescription("fail");
        Assert.fail(FAIL);
    }
}
