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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.testhelpers.points.Point;
import nl.jqno.equalsverifier.util.InternalException;
import nl.jqno.equalsverifier.util.RecursionException;

import org.junit.Test;

public class OutputTest {
	private static final String SEE_ALSO = "For more information, go to";
	private static final String WIKIPAGE_URL = "http://code.google.com/p/equalsverifier/wiki/ErrorMessages";
	
	private static final String[] BLACKLISTED_EXCEPTIONS = array(
				AssertionError.class.getSimpleName(),
				InternalException.class.getSimpleName(),
				RecursionException.class.getSimpleName());
	
	@Test
	public void assertionError() {
		EqualsVerifier<Point> ev = EqualsVerifier.forClass(Point.class);
		assertFailure(ev, array(SEE_ALSO, WIKIPAGE_URL), BLACKLISTED_EXCEPTIONS);
	}
	
	@Test
	public void internalError() {
		EqualsVerifier<Node> ev = EqualsVerifier.forClass(Node.class);
		assertFailure(ev, array(SEE_ALSO, WIKIPAGE_URL), BLACKLISTED_EXCEPTIONS);
	}

	@Test
	public void anotherException() {
		EqualsVerifier<ExceptionThrower> ev = EqualsVerifier.forClass(ExceptionThrower.class);
		assertFailure(ev, array(SEE_ALSO, UnsupportedOperationException.class.getName(),  WIKIPAGE_URL), array("null"));
	}
	
	private static String[] array(String... strings) {
		return strings;
	}
	
	private void assertFailure(EqualsVerifier<?> ev, String[] contains, String[] doesNotContain) {
		try {
			ev.verify();
			fail("No exception thrown");
		}
		catch (Throwable e) {
			String message = "" + e.getMessage();
			for (String s : contains) {
				assertTrue("<<" + message + ">> doesn't contain " + s, message.contains(s));
			}
			for (String s : doesNotContain) {
				assertFalse("<<" + message + ">> contains + " + s, message.contains(s));
			}
		}
	}
	
	@Test
	public void verbose() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream testStream = new PrintStream(baos);
		
		PrintStream syserr = System.err;
		System.setErr(testStream);
		
		try {
			EqualsVerifier.forClass(Point.class)
					.verify();
		}
		catch (AssertionError ignored) {}
		
		assertEquals(0, baos.size());
		
		try {
			EqualsVerifier.forClass(Point.class)
					.debug()
					.verify();
		}
		catch (AssertionError ignored) {}
		
		System.setErr(syserr);
		baos.close();

		byte[] expected = new byte[] {'j','a','v','a','.','l','a','n','g','.','A','s','s','e','r','t','i','o','n','E','r','r','o','r',':',' ','S','u','b','c','l','a','s','s',':'};
		assertTrue(baos.size() > expected.length);
		byte[] actual = new byte[expected.length];
		System.arraycopy(baos.toByteArray(), 0, actual, 0, expected.length);
		assertTrue(Arrays.equals(expected, actual));
	}
	
	static class ExceptionThrower {
		@Override
		public boolean equals(Object obj) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int hashCode() {
			throw new UnsupportedOperationException();
		}
	}
}
