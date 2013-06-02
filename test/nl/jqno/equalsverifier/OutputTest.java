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
import nl.jqno.equalsverifier.util.exceptions.AssertionException;
import nl.jqno.equalsverifier.util.exceptions.InternalException;
import nl.jqno.equalsverifier.util.exceptions.RecursionException;
import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

import org.junit.Test;

public class OutputTest {
	private static final String SEE_ALSO = "For more information, go to";
	private static final String WIKIPAGE_URL = "http://code.google.com/p/equalsverifier/wiki/ErrorMessages";
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
		
		assertMessageContains(SEE_ALSO, WIKIPAGE_URL);
		assertMessageDoesNotContain(BLACKLISTED_EXCEPTIONS);
		assertNoCause();
	}
	
	@Test
	public void recursionException() {
		testFor(Node.class);
		
		assertMessageContains(SEE_ALSO, WIKIPAGE_URL);
		assertMessageDoesNotContain(BLACKLISTED_EXCEPTIONS);
		assertNoCause();
	}

	@Test
	public void anyOtherExceptionWithMessage() {
		testFor(UnsupportedOperationExceptionThrower.class);
		
		assertMessageContains(UnsupportedOperationException.class.getName(), MESSAGE, SEE_ALSO, WIKIPAGE_URL);
		assertMessageDoesNotContain("null");
		assertCause(UnsupportedOperationException.class);
	}
	
	@Test
	public void anyOtherExceptionWithoutMessage() {
		testFor(IllegalStateExceptionThrower.class);
		
		assertMessageContains(IllegalStateException.class.getName(), SEE_ALSO, WIKIPAGE_URL);
		assertMessageDoesNotContain("null");
		assertCause(IllegalStateException.class);
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

	private void assertCause(Class<? extends Throwable> cause) {
		assertEquals(cause, thrown.getCause().getClass());
	}
	
	private void assertNoCause() {
		assertNull(thrown.getCause());
	}
	
	private static class UnsupportedOperationExceptionThrower {
		@Override
		public boolean equals(Object obj) {
			throw new UnsupportedOperationException(MESSAGE);
		}
		
		@Override
		public int hashCode() {
			throw new UnsupportedOperationException(MESSAGE);
		}
	}
	
	private static class IllegalStateExceptionThrower {
		@Override
		public boolean equals(Object obj) {
			throw new IllegalStateException();
		}
		
		@Override
		public int hashCode() {
			throw new IllegalStateException();
		}
	}
}
