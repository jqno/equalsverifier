/*
 * Copyright 2010, 2013 Jan Ouwens
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.testhelpers.points.Point;
import nl.jqno.equalsverifier.util.Formatter;
import nl.jqno.equalsverifier.util.exceptions.AssertionException;
import nl.jqno.equalsverifier.util.exceptions.InternalException;
import nl.jqno.equalsverifier.util.exceptions.RecursionException;
import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

import org.junit.Test;

public class OutputTest {
	private static final String SEE_ALSO = "For more information, go to";
	private static final String WIKIPAGE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
	private static final String MESSAGE = "a message for an exception";
	
	private static final String[] BLACKLISTED_EXCEPTIONS = {
			AssertionError.class.getSimpleName(),
			InternalException.class.getSimpleName(),
			RecursionException.class.getSimpleName(),
			AssertionException.class.getSimpleName(),
			ReflectionException.class.getSimpleName()
	};
	
	private Throwable thrown;
	
	@Test
	public void assertionException() {
		testFor(Point.class);
		
		assertBasicValidity();
		assertNoCause();
	}
	
	@Test
	public void assertionExceptionWithCause() {
		testFor(AssertionExceptionWithCauseThrower.class);
		
		assertBasicValidity();
		assertMessageContains(MESSAGE);
		assertMessageDoesNotContain(NullPointerException.class.getSimpleName());
		assertCause(NullPointerException.class, null);
	}
	
	@Test
	public void recursionException() {
		testFor(Node.class);
		
		assertBasicValidity();
		assertNoCause();
	}

	@Test
	public void anyOtherExceptionWithMessage() {
		testFor(UnsupportedOperationExceptionThrower.class);
		
		assertBasicValidity();
		assertMessageContains(UnsupportedOperationException.class.getName(), MESSAGE);
		assertMessageDoesNotContain("null");
		assertCause(UnsupportedOperationException.class, MESSAGE);
	}
	
	@Test
	public void anyOtherExceptionWithoutMessage() {
		testFor(IllegalStateExceptionThrower.class);
		
		assertBasicValidity();
		assertMessageContains(IllegalStateException.class.getName());
		assertMessageDoesNotContain("null");
		assertCause(IllegalStateException.class, null);
	}
	
	private void testFor(Class<?> type) {
		try {
			EqualsVerifier.forClass(type).verify();
			fail("No exception thrown");
		}
		catch (Throwable e) {
			thrown = e;
		}
	}
	
	private void assertBasicValidity() {
		assertMessageContains(SEE_ALSO, WIKIPAGE_URL);
		assertMessageDoesNotContain(BLACKLISTED_EXCEPTIONS);
	}
	
	private void assertMessageContains(String... contains) {
		String message = thrown.getMessage();
		for (String s : contains) {
			assertTrue("<<" + message + ">> doesn't contain " + s, message.contains(s));
		}
	}
	
	private void assertMessageDoesNotContain(String... doesNotContain) {
		String message = thrown.getMessage();
		for (String s : doesNotContain) {
			assertFalse("<<" + message + ">> contains + " + s, message.contains(s));
		}
	}

	private void assertCause(Class<? extends Throwable> cause, String message) {
		assertEquals(cause, thrown.getCause().getClass());
		assertEquals(message, thrown.getCause().getMessage());
	}
	
	private void assertNoCause() {
		assertNull(thrown.getCause());
	}
	
	private static class AssertionExceptionWithCauseThrower {
		@Override
		public boolean equals(Object obj) {
			Throwable cause = new NullPointerException();
			throw new AssertionException(Formatter.of(MESSAGE), cause);
		}
	}
	
	private static class UnsupportedOperationExceptionThrower {
		@Override
		public boolean equals(Object obj) {
			throw new UnsupportedOperationException(MESSAGE);
		}
	}
	
	private static class IllegalStateExceptionThrower {
		@Override
		public boolean equals(Object obj) {
			throw new IllegalStateException();
		}
	}
}
