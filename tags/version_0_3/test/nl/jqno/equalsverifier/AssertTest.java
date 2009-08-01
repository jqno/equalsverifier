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
package nl.jqno.equalsverifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class AssertTest {
	@Test
	public void assertEqualsIntSuccess() {
		Assert.assertEquals("fail", 1, 1);
	}
	
	@Test
	public void assertEqualsIntFailure() {
		checkAssertionError("fail", new Runnable() {
			public void run() {
				Assert.assertEquals("fail", 1, 2);
			}
		});
	}
	
	@Test
	public void assertEqualsObjectSuccess() {
		String one = new String("text");
		String two = new String("text");
		Assert.assertEquals("fail", one, two);
	}
	
	@Test
	public void assertEqualsObjectFailure() {
		checkAssertionError("fail", new Runnable() {
			public void run() {
				Assert.assertEquals("fail", "one", "two");
			}
		});
	}
	
	@Test
	public void assertFalseSuccess() {
		Assert.assertFalse("fail", false);
	}
	
	@Test
	public void assertFalseFailure() {
		checkAssertionError("fail", new Runnable() {
			public void run() {
				Assert.assertFalse("fail", true);
			}
		});
	}
	
	@Test
	public void assertTrueSuccess() {
		Assert.assertTrue("fail", true);
	}
	
	@Test
	public void assertTrueFailure() {
		checkAssertionError("fail", new Runnable() {
			public void run() {
				Assert.assertTrue("fail", false);
			}
		});
	}
	
	@Test
	public void failFailure() {
		checkAssertionError("fail", new Runnable() {
			public void run() {
				Assert.fail("fail");
			}
		});
	}
	
	private void checkAssertionError(String message, Runnable runnable) {
		try {
			runnable.run();
		}
		catch (AssertionError e) {
			assertEquals(message, e.getMessage());
			return;
		}
		fail("Assertion didn't fail");
	}
}
