/*
 * Copyright 2010 Jan Ouwens
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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.*;

import org.hamcrest.Matcher;
import org.junit.internal.matchers.CombinableMatcher;

public class Helper {
	public static void assertFailure(EqualsVerifier<?> equalsVerifier, String message, String... more) {
		try {
			equalsVerifier.verify();
		}
		catch (AssertionError e) {
			assertThat(e.getMessage(), containsAll(message, more));
			return;
		}
		catch (Throwable e) {
			e.printStackTrace();
			fail("Wrong exception thrown: " + e.getClass());
		}
		fail("Assertion didn't fail");
	}
	
	private static Matcher<String> containsAll(String message, String... more) {
		CombinableMatcher<String> result = both(containsString(message));
		for (String m : more) {
			result = result.and(containsString(m));
		}
		return result;
	}
}
