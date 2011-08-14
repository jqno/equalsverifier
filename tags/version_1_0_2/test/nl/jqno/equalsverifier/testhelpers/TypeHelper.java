/*
 * Copyright 2010-2011 Jan Ouwens
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
package nl.jqno.equalsverifier.testhelpers;

import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;

import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationClassRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationDoesntInherit;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationInherits;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationRuntimeRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationClassRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationDoesntInherit;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationInherits;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationRuntimeRetention;
import nl.jqno.equalsverifier.testhelpers.points.Point;

public class TypeHelper {
	private static final Object OBJECT = new Object();
	
	public static enum Enum { FIRST, SECOND }
	
	public static final class AllTypesContainer {
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
		Class<?> _type = Class.class;
		String _string = "";
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllTypesContainer)) {
				return false;
			}
			AllTypesContainer other = (AllTypesContainer)obj;
			boolean result = true;
			result &= _boolean == other._boolean;
			result &= _byte == other._byte;
			result &= _char == other._char;
			result &= Double.compare(_double, other._double) == 0;
			result &= Float.compare(_float, other._float) == 0;
			result &= _int == other._int;
			result &= _long == other._long;
			result &= _short == other._short;
			result &= _Boolean == other._Boolean;
			result &= _Byte == other._Byte;
			result &= _Char == other._Char;
			result &= _Double == null ? other._Double == null : Double.compare(_Double, other._Double) == 0;
			result &= _Float == null ? other._Float == null : Float.compare(_Float, other._Float) == 0;
			result &= _Int == other._Int;
			result &= _Long == other._Long;
			result &= _Short == other._Short;
			result &= _enum == other._enum;
			result &= Arrays.equals(_array, other._array);
			result &= nullSafeEquals(_object, other._object);
			result &= _type == other._type;
			result &= nullSafeEquals(_string, other._string);
			return result;
		}
	}
	
	public static final class AllArrayTypesContainer {
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
			boolean result = true;
			result &= Arrays.equals(booleans, other.booleans);
			result &= Arrays.equals(bytes, other.bytes);
			result &= Arrays.equals(chars, other.chars);
			result &= Arrays.equals(doubles, other.doubles);
			result &= Arrays.equals(floats, other.floats);
			result &= Arrays.equals(ints, other.ints);
			result &= Arrays.equals(longs, other.longs);
			result &= Arrays.equals(shorts, other.shorts);
			result &= Arrays.equals(Booleans, other.Booleans);
			result &= Arrays.equals(Bytes, other.Bytes);
			result &= Arrays.equals(Characters, other.Characters);
			result &= Arrays.equals(Doubles, other.Doubles);
			result &= Arrays.equals(Floats, other.Floats);
			result &= Arrays.equals(Integers, other.Integers);
			result &= Arrays.equals(Longs, other.Longs);
			result &= Arrays.equals(Shorts, other.Shorts);
			result &= Arrays.equals(enums, other.enums);
			result &= Arrays.deepEquals(arrays, other.arrays);
			result &= Arrays.equals(objects, other.objects);
			result &= Arrays.equals(classes, other.classes);
			result &= Arrays.equals(strings, other.strings);
			return result;
		}
	}
	
	public static class RecursiveApiClassesContainer {
		Calendar calendar;
		Date date;
		DateFormat dateFormat;
		Formatter formatter;
		GregorianCalendar gregorianCalendar;
		Locale locale;
		Pattern pattern;
		Scanner scanner;
		TimeZone timeZone;
		BigDecimal bigDecimal;
		BigInteger bigInteger;
		Throwable throwable;
		Exception exception;
	}
	
	public static class AllRecursiveCollectionImplementationsContainer {
		CopyOnWriteArrayList<?> copyOnWriteArrayList;
		LinkedList<?> linkedList;
		
		ConcurrentHashMap<?,?> concurrentHashMap;
		EnumMap<?,?> enumMap;
		HashMap<?,?> hashMap;
		Hashtable<?,?> hashtable;
		LinkedHashMap<?,?> linkedHashMap;
		Properties properties;
		TreeMap<?,?> treeMap;
		WeakHashMap<?,?> weakHashMap;
		
		CopyOnWriteArraySet<?> copyOnWriteArraySet;
		EnumSet<?> enumSet;
		HashSet<?> hashSet;
		LinkedHashSet<?> linkedHashSet;
		TreeSet<?> treeSet;
		
		ArrayBlockingQueue<?> arrayBlockingQueue;
		ConcurrentLinkedQueue<?> concurrentLinkedQueue;
		DelayQueue<?> delayQueue;
		LinkedBlockingQueue<?> linkedBlockingQueue;
		PriorityBlockingQueue<?> priorityBlockingQueue;
		SynchronousQueue<?> synchronousQueue;
	}
	
	public static class DifferentAccessModifiersFieldContainer {
		@SuppressWarnings("unused")
		private final int i = 0;
		final int j = 0;
		protected final int k = 0;
		public final int l = 0;
		@SuppressWarnings("unused")
		private static final int I = 0;
		final static int J = 0;
		protected static final int K = 0;
		public static final int L = 0;
	}
	
	public static class DifferentAccessModifiersSubFieldContainer extends DifferentAccessModifiersFieldContainer {
		@SuppressWarnings("unused")
		private final String a = "";
		final String b = "";
		protected final String c = "";
		public final String d = "";
	}

	public static class EmptySubFieldContainer extends DifferentAccessModifiersFieldContainer {}
	
	public static class SubEmptySubFieldContainer extends EmptySubFieldContainer {
		public long field = 0;
	}

	public static interface Interface {}
	
	public static abstract class AbstractClass {
		int field;
	}
	
	public static class Outer {
		public class Inner {
			public Outer getOuter() {
				return Outer.this;
			}
		}
	}
	
	public static final class InterfaceContainer {
		public Interface field;
	}
	
	public static final class AbstractClassContainer {
		public AbstractClass field;
	}
	
	public static final class AbstractAndInterfaceArrayContainer {
		public AbstractClass[] abstractClasses = new AbstractClass[] { null };
		public Interface[] interfaces = new Interface[] { null };
	}

	public static final class ObjectContainer {
		public Object field = new Object();
	}
	
	public static final class FinalContainer {
		final Object field = new Object();
	}
	
	public static final class StaticContainer {
		static Object field = new Object();
	}
	
	public static final class TransientContainer {
		transient Object field = new Object();
	}
	
	public static class PrivateObjectContainer {
		private Object field = new Object();
		
		public Object get() {
			return field;
		}
	}
	
	public static class ArrayContainer {
		int[] field;
	}
	
	public static final class PrimitiveContainer {
		public int field;
	}
	
	public static final class StaticFinalContainer {
		public static final int CONST = 42;
		public static final Object OBJECT = new Object();
	}
	
	public static class NoFields {}
	
	public static class NoFieldsSubWithFields extends NoFields {
		public Object field;
	}
	
	public static enum OneElementEnum { ONE }
	
	public static enum EmptyEnum {}
	
	public static final class PointArrayContainer {
		public Point[] points = { new Point(1, 2) };
	}

	@TypeAnnotationRuntimeRetention
	public static class AnnotatedWithRuntime {}

	@TypeAnnotationClassRetention
	public static class AnnotatedWithClass {}
	
	@TypeAnnotationRuntimeRetention
	@TypeAnnotationClassRetention
	public static class AnnotatedWithBoth {}
	
	public static class AnnotatedFields {
		@FieldAnnotationRuntimeRetention
		public int runtimeRetention;
		
		@FieldAnnotationClassRetention
		public int classRetention;
		
		@FieldAnnotationRuntimeRetention
		@FieldAnnotationClassRetention
		public int bothRetentions;
		
		public int noRetention;
	}
	
	@TypeAnnotationInherits
	@TypeAnnotationDoesntInherit
	public static class SuperclassWithAnnotations {
		@FieldAnnotationInherits
		public int inherits;
		
		@FieldAnnotationDoesntInherit
		public int doesntInherit;
	}
	
	public static class SubclassWithAnnotations extends SuperclassWithAnnotations {}

	public static abstract class AbstractEqualsAndHashCode {
		@Override
		public abstract boolean equals(Object obj);
		
		@Override
		public abstract int hashCode();
	}
	
	@SuppressWarnings("serial")
	public static final class LoadedBySystemClassLoader extends Exception {}
}
