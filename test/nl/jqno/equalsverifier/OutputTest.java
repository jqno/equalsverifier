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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class OutputTest {
	@Test(expected=UnsupportedOperationException.class)
	public void anotherException() {
		EqualsVerifier.forClass(ExceptionThrower.class).verify();
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
