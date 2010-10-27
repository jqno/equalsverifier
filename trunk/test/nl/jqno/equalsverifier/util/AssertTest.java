/*
 * Copyright 2009 Jan Ouwens
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

import static nl.jqno.equalsverifier.Helper.assertAssertionError;

import org.junit.Test;

public class AssertTest {
	@Test
	public void assertEqualsIntSuccess() {
		Assert.assertEquals("fail", 1, 1);
	}
	
	@Test
	public void assertEqualsIntFailure() {
		Runnable r = new Runnable() {
			public void run() {
				Assert.assertEquals("fail", 1, 2);
			}
		};
		assertAssertionError(r, "fail");
	}
	
	@Test
	public void assertEqualsObjectSuccess() {
		String red = new String("text");
		String black = new String("text");
		Assert.assertEquals("fail", red, black);
	}
	
	@Test
	public void assertEqualsObjectFailure() {
		Runnable r = new Runnable() {
			public void run() {
				Assert.assertEquals("fail", "one", "two");
			}
		};
		assertAssertionError(r, "fail");
	}
	
	@Test
	public void assertFalseSuccess() {
		Assert.assertFalse("fail", false);
	}
	
	@Test
	public void assertFalseFailure() {
		Runnable r = new Runnable() {
			public void run() {
				Assert.assertFalse("fail", true);
			}
		};
		assertAssertionError(r, "fail");
	}
	
	@Test
	public void assertTrueSuccess() {
		Assert.assertTrue("fail", true);
	}
	
	@Test
	public void assertTrueFailure() {
		Runnable r = new Runnable() {
			public void run() {
				Assert.assertTrue("fail", false);
			}
		};
		assertAssertionError(r, "fail");
	}
	
	@Test
	public void failFailure() {
		Runnable r = new Runnable() {
			public void run() {
				Assert.fail("fail");
			}
		};
		assertAssertionError(r, "fail");
	}
}
