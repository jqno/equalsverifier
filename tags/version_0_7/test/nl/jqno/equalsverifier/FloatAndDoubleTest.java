/*
 * Copyright 2009-2010 Jan Ouwens
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

import static nl.jqno.equalsverifier.Helper.assertFailure;

import org.junit.Test;

public class FloatAndDoubleTest {
	private static final String FLOAT = "Float: equals doesn't use Float.compare for field";
	private static final String DOUBLE = "Double: equals doesn't use Double.compare for field";

	@Test
	public void primitiveUseFloatDotCompare() {
		EqualsVerifier<PrimitiveFloatContainer> ev = EqualsVerifier.forClass(PrimitiveFloatContainer.class);
		assertFailure(ev, FLOAT, "f");
	}
	
	@Test
	public void objectUseFloatDotCompare() {
		EqualsVerifier<ObjectFloatContainer> ev = EqualsVerifier.forClass(ObjectFloatContainer.class);
		assertFailure(ev, FLOAT, "f");
	}
	
	@Test
	public void primitiveUseDoubleDotCompare() {
		EqualsVerifier<PrimitiveDoubleContainer> ev = EqualsVerifier.forClass(PrimitiveDoubleContainer.class);
		assertFailure(ev, DOUBLE, "d");
	}
	
	@Test
	public void objectUseDoubleDotCompare() {
		EqualsVerifier<ObjectDoubleContainer> ev = EqualsVerifier.forClass(ObjectDoubleContainer.class);
		assertFailure(ev, DOUBLE, "d");
	}
	
	static final class PrimitiveFloatContainer {
		final float f;
		
		PrimitiveFloatContainer(float f) {
			this.f = f;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveFloatContainer)) {
				return false;
			}
			return f == ((PrimitiveFloatContainer)obj).f;
		}
		
		@Override
		public int hashCode() {
			return Float.floatToIntBits(f);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + f;
		}
	}
	
	static final class ObjectFloatContainer {
		final Float f;
		
		ObjectFloatContainer(Float f) {
			this.f = f;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectFloatContainer)) {
				return false;
			}
			return f == ((ObjectFloatContainer)obj).f;
		}
		
		@Override
		public int hashCode() {
			return (f == null) ? 0 : Float.floatToIntBits(f);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + f;
		}
	}
	
	static final class PrimitiveDoubleContainer {
		final double d;
		
		PrimitiveDoubleContainer(double d) {
			this.d = d;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveDoubleContainer)) {
				return false;
			}
			return d == ((PrimitiveDoubleContainer)obj).d;
		}
		
		@Override
		public int hashCode() {
			long h = Double.doubleToLongBits(d);
			return (int)(h ^ (h >>> 32));
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + d;
		}
	}
	
	static final class ObjectDoubleContainer {
		final Double d;
		
		ObjectDoubleContainer(Double d) {
			this.d = d;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectDoubleContainer)) {
				return false;
			}
			return d == ((ObjectDoubleContainer)obj).d;
		}
		
		@Override
		public int hashCode() {
			if (d == null) {
				return 0;
			}
			long h = Double.doubleToLongBits(d);
			return (int)(h ^ (h >>> 32));
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + d;
		}
	}
}
