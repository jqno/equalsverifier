/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.instantiator;

import java.util.Arrays;

public class TypeHelper {
	private static enum Enum { FIRST, SECOND }
	private static final Object OBJECT = new Object();
	
	static final class AllTypesContainer {
		boolean _boolean = false;
		byte _byte = 0;
		char _char = '\u0000';
		double _double = 0.0D;
		float _float = 0.0F;
		int _int = 0;
		long _long = 0L;
		short _short = 0;

		Boolean _Boolean = false;
		Byte _Byte = 0;
		Character _Char = '\u0000';
		Double _Double = 0.0D;
		Float _Float = 0.0F;
		Integer _Int = 0;
		Long _Long = 0L;
		Short _Short = 0;
		
		Enum _enum = Enum.FIRST;
		int[] _array = {1, 2, 3};
		Object _object = OBJECT;
		Class<?> _klass = Class.class;
		String _string = "";
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllTypesContainer)) {
				return false;
			}
			AllTypesContainer other = (AllTypesContainer)obj;
			return
					_boolean == other._boolean &&
					_byte == other._byte &&
					_char == other._char &&
					(Double.compare(_double, other._double) == 0) &&
					(Float.compare(_float, other._float) == 0) &&
					_int == other._int &&
					_long == other._long &&
					_short == other._short &&
					_Boolean == other._Boolean &&
					_Byte == other._Byte &&
					_Char == other._Char &&
					((_Double == null || other._Double == null) ?
							_Double == other._Double :
							Double.compare(_Double, other._Double) == 0) &&
					((_Float == null || other._Float == null) ?
							_Float == other._Float :
							Float.compare(_Float, other._Float) == 0) &&
					_Int == other._Int &&
					_Long == other._Long &&
					_Short == other._Short &&
					_enum == other._enum &&
					Arrays.equals(_array, other._array) &&
					(_object == null ? other._object == null : _object.equals(other._object)) &&
					_klass == other._klass &&
					(_string == null ? other._string == null : _string.equals(other._string));
		}
	}
	
	static final class AllArrayTypesContainer {
		boolean[] booleans = { true };
		byte[] bytes = { 1 };
		char[] chars = { 'a' };
		double[] doubles = { 1.0D };
		float[] floats = { 1.0F };
		int[] ints = { 1 };
		long[] longs = { 1L };
		short[] shorts = { 1 };

		Boolean[] Booleans = { true };
		Byte[] Bytes = { 1 };
		Character[] Characters = { 'a' };
		Double[] Doubles = { 1.0D };
		Float[] Floats = { 1.0F };
		Integer[] Integers = { 1 };
		Long[] Longs = { 1L };
		Short[] Shorts = { 1 };

		Enum[] enums = { Enum.FIRST };
		int[][] arrays = { { 1 } };
		Object[] objects = { OBJECT };
		Class<?>[] classes = { Class.class };
		String[] strings = { "1" };

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllArrayTypesContainer)) {
				return false;
			}
			AllArrayTypesContainer other = (AllArrayTypesContainer)obj;
			return
					Arrays.equals(booleans, other.booleans) &&
					Arrays.equals(bytes, other.bytes) &&
					Arrays.equals(chars, other.chars) &&
					Arrays.equals(doubles, other.doubles) &&
					Arrays.equals(floats, other.floats) &&
					Arrays.equals(ints, other.ints) &&
					Arrays.equals(longs, other.longs) &&
					Arrays.equals(shorts, other.shorts) &&
					Arrays.equals(Booleans, other.Booleans) &&
					Arrays.equals(Bytes, other.Bytes) &&
					Arrays.equals(Characters, other.Characters) &&
					Arrays.equals(Doubles, other.Doubles) &&
					Arrays.equals(Floats, other.Floats) &&
					Arrays.equals(Integers, other.Integers) &&
					Arrays.equals(Longs, other.Longs) &&
					Arrays.equals(Shorts, other.Shorts) &&
					Arrays.equals(enums, other.enums) &&
					Arrays.deepEquals(arrays, other.arrays) &&
					Arrays.equals(objects, other.objects) &&
					Arrays.equals(classes, other.classes) &&
					Arrays.equals(strings, other.strings);
		}
	}

	static interface Interface {}
	
	static abstract class AbstractClass {
		public int i; 
	}
	
	static final class InterfaceContainer {
		public Interface _interface;
	}
	
	static final class AbstractClassContainer {
		public AbstractClass ac;
	}
	
	static final class AbstractAndInterfaceArrayContainer {
		public AbstractClass[] abstractClasses = new AbstractClass[] { null };
		public Interface[] interfaces = new Interface[] { null };
	}

	static final class ObjectContainer {
		public Object _object = new Object();
	}
	
	static final class PrimitiveContainer {
		public int i = 10;
	}
	
	static final class StaticFinalContainer {
		public static final int CONST = 42;
		public static final Object OBJECT = new Object();
	}
}
