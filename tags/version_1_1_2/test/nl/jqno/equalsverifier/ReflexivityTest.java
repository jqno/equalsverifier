/*
 * Copyright 2009-2010,2012 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.testhelpers.points.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Test;

public class ReflexivityTest {
	@Test
	public void reflexivity() {
		EqualsVerifier<ReflexivityBrokenPoint> ev = EqualsVerifier.forClass(ReflexivityBrokenPoint.class);
		assertFailure(ev, "Reflexivity", "object does not equal itself", ReflexivityBrokenPoint.class.getSimpleName());
	}
	
	@Test
	public void wrongCast() {
		EqualsVerifier<WrongCast> ev = EqualsVerifier.forClass(WrongCast.class);
		assertFailure(ev, "Reflexivity", "object does not equal an identical copy of itself", WrongCast.class.getSimpleName());
	}
	
	@Test
	public void suppressIdenticalCopy() {
		EqualsVerifier<SuperCaller> ev = EqualsVerifier.forClass(SuperCaller.class);
		assertFailure(ev, "Reflexivity", "identical copy");
		
		EqualsVerifier.forClass(SuperCaller.class)
				.suppress(Warning.IDENTICAL_COPY)
				.verify();
	}
	
	@Test
	public void failOnIdenticalCopy() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forClass(FinalPoint.class).suppress(Warning.IDENTICAL_COPY);
		assertFailure(ev, "Unnecessary suppression", "IDENTICAL_COPY");
	}
	
	static class ReflexivityBrokenPoint extends Point {
		// Instantiator.scramble will flip this boolean.
		private boolean broken = false;
		
		public ReflexivityBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (broken && this == obj) {
				return false;
			}
			return super.equals(obj);
		}
	}
	
	static final class WrongCast {
		private final int foo;
		
		public WrongCast(int foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof SomethingCompletelyDifferent)) {
				return false;
			}
			WrongCast other = (WrongCast)obj;
			return foo == other.foo;
		}
		
		@Override
		public int hashCode() {
			return foo;
		}
	}
	
	static class SomethingCompletelyDifferent {}
	
	static final class SuperCaller {
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
}
