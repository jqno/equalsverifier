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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EqualsVerifierTestBase {
	public void verifyFailure(String message, EqualsVerifier<?> equalsVerifier) {
		try {
			equalsVerifier.verify();
		}
		catch (AssertionError e) {
			// Using startsWith because Assert class sometimes adds extra info to the error message.
			assertTrue("Assertion has incorrect message: expected <" + message + "> but was <" + e.getMessage() + ">",
					e.getMessage().startsWith(message));
			return;
		}
		catch (Throwable e) {
			e.printStackTrace();
			fail("Wrong exception thrown: " + e.getClass());
		}
		fail("Assertion didn't fail");
	}
}
