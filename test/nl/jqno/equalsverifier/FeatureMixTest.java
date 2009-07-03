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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class FeatureMixTest extends EqualsVerifierTestBase {
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
					.with(Feature.VERBOSE)
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
	
	@Test
	public void notFinalAndMutable() {
		EqualsVerifier<MutablePoint> ev1 = EqualsVerifier.forClass(MutablePoint.class);
		ev1.with(Feature.WEAK_INHERITANCE_CHECK);
		verifyFailure("Mutability:", ev1);
		
		EqualsVerifier<MutablePoint> ev2 = EqualsVerifier.forClass(MutablePoint.class);
		ev2.with(Feature.ALLOW_NONFINAL_FIELDS);
		verifyFailure("Subclass:", ev2);
		
		EqualsVerifier.forClass(MutablePoint.class)
				.with(Feature.WEAK_INHERITANCE_CHECK, Feature.ALLOW_NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void notFinalAndNeverNull() {
		EqualsVerifier<NeverNullColorContainer> ev1 = EqualsVerifier.forClass(NeverNullColorContainer.class);
		ev1.with(Feature.WEAK_INHERITANCE_CHECK);
		verifyFailure("Non-nullity:", ev1);
		
		EqualsVerifier<NeverNullColorContainer> ev2 = EqualsVerifier.forClass(NeverNullColorContainer.class);
		ev2.with(Feature.FIELDS_ARE_NEVER_NULL);
		verifyFailure("Subclass:", ev2);
		
		EqualsVerifier.forClass(NeverNullColorContainer.class)
				.with(Feature.WEAK_INHERITANCE_CHECK, Feature.FIELDS_ARE_NEVER_NULL)
				.verify();
	}
	
	@Test
	public void notFinalAndNeverNullAndNonFinal() {
		// It could happen! Just make sure your setters check for null :).
		
		EqualsVerifier<NeverNullAndMutableColorContainer> ev1 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev1.with(Feature.WEAK_INHERITANCE_CHECK, Feature.FIELDS_ARE_NEVER_NULL);
		verifyFailure("Mutability:", ev1);
		
		EqualsVerifier<NeverNullAndMutableColorContainer> ev2 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev2.with(Feature.WEAK_INHERITANCE_CHECK, Feature.ALLOW_NONFINAL_FIELDS);
		verifyFailure("Non-nullity:", ev2);
		
		EqualsVerifier<NeverNullAndMutableColorContainer> ev3 = EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class);
		ev3.with(Feature.ALLOW_NONFINAL_FIELDS, Feature.FIELDS_ARE_NEVER_NULL);
		verifyFailure("Subclass:", ev3);
		
		EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
				.with(Feature.WEAK_INHERITANCE_CHECK, Feature.FIELDS_ARE_NEVER_NULL, Feature.ALLOW_NONFINAL_FIELDS)
				.verify();
	}

	private static class MutablePoint {
		private int x;
		private int y;
		
		public MutablePoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof MutablePoint)) {
				return false;
			}
			MutablePoint other = (MutablePoint)obj;
			return x == other.x && y == other.y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	private static class NeverNullColorContainer {
		private final Color color;
		
		public NeverNullColorContainer(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NeverNullColorContainer)) {
				return false;
			}
			return color == ((NeverNullColorContainer)obj).color;
		}
		
		@Override
		public int hashCode() {
			return color.hashCode();
		}
	}
	
	private static class NeverNullAndMutableColorContainer {
		private Color color;
		
		public NeverNullAndMutableColorContainer(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NeverNullAndMutableColorContainer)) {
				return false;
			}
			return color == ((NeverNullAndMutableColorContainer)obj).color;
		}
		
		@Override
		public int hashCode() {
			return color.hashCode();
		}
	}
}
