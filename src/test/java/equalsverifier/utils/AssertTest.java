package equalsverifier.utils;

import equalsverifier.utils.exceptions.AssertionException;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

import static equalsverifier.testhelpers.Util.coverThePrivateConstructor;

public class AssertTest extends ExpectedExceptionTestBase {
    private static final Formatter FAIL = Formatter.of("fail");

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Assert.class);
    }

    @Test
    public void assertEqualsObjectSuccess() {
        String red = new String("text");
        String black = new String("text");
        Assert.assertEquals(FAIL, red, black);
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
