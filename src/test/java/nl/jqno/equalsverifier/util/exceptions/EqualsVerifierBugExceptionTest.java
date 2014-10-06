/*
 * Copyright 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.util.exceptions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import nl.jqno.equalsverifier.util.exceptions.EqualsVerifierBugException;

import org.junit.Test;

public class EqualsVerifierBugExceptionTest {
	private EqualsVerifierBugException actual;
	
	@Test
	public void exceptionHasNoSpecificMessage() {
		actual = new EqualsVerifierBugException();
		
		assertNoMessage();
		assertNoCause();
	}
	
	@Test
	public void exceptionHasSpecificMessage() {
		String message = "my message";
		actual = new EqualsVerifierBugException(message);
		
		assertMessage(message);
		assertNoCause();
	}
	
	@Test
	public void exceptionHasACause() {
		Throwable cause = new IllegalStateException("cause of the bug");
		actual = new EqualsVerifierBugException(cause);
		
		assertNoMessage();
		assertCause(cause);
	}
	
	@Test
	public void exceptionHasMessageAndCause() {
		String message = "some message";
		Throwable cause = new IllegalArgumentException("some cause");
		actual = new EqualsVerifierBugException(message, cause);
		
		assertMessage(message);
		assertCause(cause);
	}
	
	private void assertNoMessage() {
		assertMessagePreamble();
		assertThat(actual.getMessage(), not(containsString("\n")));
	}
	
	private void assertMessage(String message) {
		assertMessagePreamble();
		assertThat(actual.getMessage(), containsString("\n"));
		assertThat(actual.getMessage(), containsString(message));
	}
	
	private void assertMessagePreamble() {
		assertThat(actual.getMessage(), containsString("This is a bug in EqualsVerifier"));
	}
	
	private void assertNoCause() {
		assertThat(actual.getCause(), is(nullValue()));
	}

	private void assertCause(Throwable cause) {
		assertThat(actual.getCause(), is(cause));
	}
}
