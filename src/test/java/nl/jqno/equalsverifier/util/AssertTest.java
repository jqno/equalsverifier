/*
 * Copyright 2009-2010, 2013 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.util;

import nl.jqno.equalsverifier.util.exceptions.AssertionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AssertTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final Formatter FAIL = Formatter.of("fail");
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
