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

import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class HashCodeTest extends EqualsVerifierTestBase {
	@Test
	public void invalidHashCode() {
		EqualsVerifier<HashCodeBrokenPoint> ev = EqualsVerifier.forClass(HashCodeBrokenPoint.class);
		verifyFailure("hashCode: hashCode for HashCodeBrokenPoint:1,1 should be equal to" +
				" hashCode for HashCodeBrokenPoint:1,1.", ev);
	}
	
	@Test
	public void nullAndZeroAndEmptyStringHaveHashCode0() {
		EqualsVerifier.forClass(ZeroablesContainer.class).verify();
	}
	
	static class HashCodeBrokenPoint extends Point {
		public HashCodeBrokenPoint(int x, int y) {
			super(x, y);
		}

		@Override
		public int hashCode() {
			return new Object().hashCode();
		}
	}
	
	static final class ZeroablesContainer {
		final String _string;
		final Byte _byte;
		final Character _char;
		final Double _double;
		final Float _float;
		final Integer _int;
		final Long _long;
		final Short _short;
		
		ZeroablesContainer(String _string, Byte _byte, Character _char, Double _double,
				Float _float, Integer _int, Long _long, Short _short) {
			this._string = _string;
			this._byte = _byte;
			this._char = _char;
			this._double = _double;
			this._float = _float;
			this._int = _int;
			this._long = _long;
			this._short = _short;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ZeroablesContainer)) {
				return false;
			}
			ZeroablesContainer other = (ZeroablesContainer)obj;
			return
				(_string == null || other._string == null) ?
						_string == other._string :
						_string.equals(other._string) &&
				_byte == other._byte &&
				_char == other._char &&
				(_double == null || other._double == null ?
						_double == other._double :
						Double.compare(_double, other._double) == 0) &&
				(_float == null || other._float == null ?
						_float == other._float :
						Float.compare(_float, other._float) == 0) &&
				_int == other._int &&
				_long == other._long &&
				_short == other._short;
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + (_string == null ? 0 : _string.hashCode());
			result = 31 * result + (_byte == null ? 0 : _byte);
			result = 31 * result + (_char == null ? 0 : _char);
			result = 31 * result + (_double == null ? 0 : _double.hashCode());
			result = 31 * result + (_float == null ? 0 : _float.hashCode());
			result = 31 * result + (_int == null ? 0 : _int);
			result = 31 * result + (_long == null ? 0 : _long.hashCode());
			result = 31 * result + (_short == null ? 0 : _short);
			return result;
		}
	}
}
