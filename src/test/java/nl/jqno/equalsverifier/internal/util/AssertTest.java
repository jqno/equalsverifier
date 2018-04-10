package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

public class AssertTest {
    private static final Formatter FAIL = Formatter.of("fail");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        thrown.expect(AssertionException.class);
        thrown.expectMessage("fail");
        Assert.assertEquals(FAIL, "one", "two");
    }

    @Test
    public void assertFalseSuccess() {
        Assert.assertFalse(FAIL, false);
    }

    @Test
    public void assertFalseFailure() {
        thrown.expect(AssertionException.class);
        thrown.expectMessage("fail");
        Assert.assertFalse(FAIL, true);
    }

    @Test
    public void assertTrueSuccess() {
        Assert.assertTrue(FAIL, true);
    }

    @Test
    public void assertTrueFailure() {
        thrown.expect(AssertionException.class);
        thrown.expectMessage("fail");
        Assert.assertTrue(FAIL, false);
    }

    @Test
    public void failFailure() {
        thrown.expect(AssertionException.class);
        thrown.expectMessage("fail");
        Assert.fail(FAIL);
    }
}
