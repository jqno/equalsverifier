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

import static nl.jqno.equalsverifier.Assert.assertTrue;
import static nl.jqno.equalsverifier.Assert.fail;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import nl.jqno.instantiator.Instantiator;

/**
 * {@code EqualsVerifier} can be used in unit tests to verify whether the
 * contract for the {@code equals} and {@code hashCode} methods in a class is
 * met. The contracts are described in the Javadoc comments for the
 * {@link java.lang.Object} class.
 * <p>
 * Use, within unit test method, as follows:<br>
 * - Create an instance of {@link EqualsVerifier}. Either call
 * {@link #forExamples(Object, Object, Object...)} to supply at least two
 * instances of the class under test that are not equal to one another, or
 * call {@link #forClass(Class)} to supply a reference to the class itself to
 * let the {@link EqualsVerifier} instantiate objects. Also,
 * {@link #forRelaxedEqualExamples(Object, Object, Object...)} can be used if
 * the class under test has relaxed equality rules, for example, if the
 * contents of two fields of the same type can be interchanged without breaking
 * equality.<br>
 * - If the class under test is designed for inheritance, and the
 * {@code equals} and {@code hashCode} methods can be overridden, an instance
 * of the class is not permitted to be equal to an instance of a subclass, even
 * though all the relevant fields are equal. Call
 * {@link #withRedefinedSubclass(Class)} to supply a reference to such a
 * subclass, or call {@link #with(Feature...)} with
 * {@link Feature#WEAK_INHERITANCE_CHECK} to disable the check.<br>
 * - Call {@link #with(Feature...)} to modify the behaviour of
 * {@code EqualsVerifier}.<br>
 * - Call {@link #verify()} to perform the actual verifications.
 * <p>
 * Example use:
 * 
 * <pre>{@code
 * EqualsVerifier.forClass(My.class).verify();
 * }</pre>
 * 
 * Or, with some features enabled:
 * 
 * <pre>{@code
 * EqualsVerifier.forClass(My.class)
 *     .with(Feature.ALLOW_MUTABLE_FIELDS, Feature.FIELDS_ARE_NEVER_NULL)
 *     .verify();
 * }</pre>
 * 
 * The following properties are verified:<br>
 * - Preconditions for {@link EqualsVerifier} itself.<br>
 * - Reflexivity and symmetry of the {@code equals} method.<br>
 * - Symmetry and transitivity of the {@code equals} method within an
 * inheritance hierarchy, when applicable.<br>
 * - Consistency (by repeatedly calling {@code equals}).<br>
 * - "Non-nullity".<br>
 * - That {@code equals}, {@code hashCode} and {@code toString} not be able to
 * throw {@link NullPointerException}. (Optional)<br>
 * - The {@code hashCode} contract.<br>
 * - That {@code equals} and {@code hashCode} be defined in terms of
 * the same fields.<br>
 * - Final-ness of the fields in terms of which {@code equals} and
 * {@code hashCode} are defined. (Optional)<br>
 * - The finality of the fields in terms of which {@code equals} and
 * {@code hashCode} are defined. (Optional)<br>
 * - Finality of the class under test and of the {@code equals} method
 * itself, when applicable.
 * <p>
 * Dependencies:<br>
 * - objenesis 1.1: http://code.google.com/p/objenesis/<br>
 * - cglib-nodep 2.2: http://cglib.sourceforge.net/
 * <p>
 * For more information, see the documentation at
 * http://equalsverifier.googlecode.com/
 * 
 * @param <T> The class under test.
 * 
 * @author Jan Ouwens
 * @see java.lang.Object#equals(Object)
 * @see java.lang.Object#hashCode()
 */
public final class EqualsVerifier<T> {
	private final Class<T> klass;
	private final Instantiator<T> instantiator;
	private final List<T> equalExamples;
	private final List<T> unequalExamples;
	
	private final EnumSet<Feature> features = EnumSet.noneOf(Feature.class);
	private Class<? extends T> redefinedSubclass = null;
	
	@SuppressWarnings("serial")
	private static final List<Class<?>> CORE_CLASSES = new ArrayList<Class<?>>() {{
		add(Object.class);
		add(Class.class);
		add(String.class);
		add(Byte.class);
		add(Double.class);
		add(Float.class);
		add(Integer.class);
		add(Long.class);
		add(Short.class);
	}};
	
	/**
	 * Factory method. For general use.
	 * 
	 * @param klass The class for which the {@code equals} method should be
	 * 				tested.
	 */
	public static <T> EqualsVerifier<T> forClass(Class<T> klass) {
		Instantiator<T> instantiator = Instantiator.forClass(klass);
		List<T> equalExamples = new ArrayList<T>();
		List<T> unequalExamples = new ArrayList<T>();

		return new EqualsVerifier<T>(klass, instantiator, equalExamples, unequalExamples);
	}
	
	/**
	 * Factory method. Use when it is necessary or desired to give explicit
	 * examples of instances of T. It's theoretically possible that
	 * {@link #forClass(Class)} doesn't generate the examples that expose a
	 * certain weakness in the {@code equals} implementation. In such cases,
	 * this method can be used.
	 * 
	 * @param first An instance of T.
	 * @param second Another instance of T, which is unequal to {@code first}.
	 * @param tail An array of instances of T, all of which are unequal to one
	 *		 		another and to {@code first} and {@code second}. May also
	 *				contain instances of subclasses of T.
	 */
	public static <T> EqualsVerifier<T> forExamples(T first, T second, T... tail) {
		List<T> equalExamples = new ArrayList<T>();
		List<T> unequalExamples = buildList2(first, second, tail);
		
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>)first.getClass();
		Instantiator<T> instantiator = Instantiator.forClass(klass);
		
		return new EqualsVerifier<T>(klass, instantiator, equalExamples, unequalExamples);
	}
	
	/**
	 * Factory method. For use when T is a class which has relaxed equality
	 * rules. For example, if the contents of two fields of the same type can
	 * be interchanged without breaking equality.
	 * 
	 * Asks for a list of equal, but not identical, instances of T.
	 * 
	 * Using this factory method requires that
	 * {@link RelaxedEqualsVerifierHelper#andUnequalExamples(Object, Object...)}
	 * be called to supply a list of unequal instances of T.
	 * 
	 * @param first An instance of T.
	 * @param second Another instance of T, which is equal, but not identical,
	 * 				to {@code first}.
	 * @param tail An array of instances of T, all of which are equal, but not
	 * 				identical, to one another and to {@code first} and
	 * 				{@code second}.
	 */
	public static <T> RelaxedEqualsVerifierHelper<T> forRelaxedEqualExamples(T first, T second, T... tail) {
		List<T> examples = buildList2(first, second, tail);
		
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>)first.getClass();
		Instantiator<T> instantiator = Instantiator.forClass(klass);
		
		return new RelaxedEqualsVerifierHelper<T>(klass, instantiator, examples);
	}

	/**
	 * Private constructor. Call {@link #forClass(Class)},
	 * {@link #forExamples(Object, Object, Object...)} or
	 * {@link #forRelaxedEqualExamples(Object, Object, Object...)} instead.
	 */
	private EqualsVerifier(Class<T> klass, Instantiator<T> instantiator, List<T> equalExamples, List<T> unequalExamples) {
		this.klass = klass;
		this.instantiator = instantiator;
		this.equalExamples = equalExamples;
		this.unequalExamples = unequalExamples;
	}
	
	/**
	 * Adds features to the {@code EqualsVerifier}. These features modify the
	 * behaviour of the {@code EqualsVerifier}. See {@link Feature} to see what
	 * features are available.
	 * 
	 * @param features A list of features to add to the {@code EqualsVerifier}.
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> with(Feature... features) {
		for (Feature feature : features) {
			this.features.add(feature);
		}
		return this;
	}
	
	/**
	 * Adds prefabricated values for instance fields of classes that
	 * EqualsVerifier cannot instantiate by itself.
	 * 
	 * @param <S> The class of the prefabricated values.
	 * @param otherKlass The class of the prefabricated values.
	 * @param first An instance of {@code S}.
	 * @param second An instance of {@code S}.
	 * @return {@code this}, for easy method chaining.
	 * @throws NullPointerException If either {@code first} or {@code second}
	 * 				is null.
	 * @throws IllegalArgumentException If {@code first} equals {@code second}.
	 */
	public <S> EqualsVerifier<T> withPrefabValues(Class<S> otherKlass, S first, S second) {
		instantiator.addPrefabValues(otherKlass, first, second);
		return this;
	}
	
	/**
	 * Supplies a reference to a subclass of T in which {@code equals} is
	 * overridden. Calling this method is mandatory if {@code equals} is not
	 * final and a strong verification is performed.
	 * <p>
	 * Note that, for each subclass that overrides {@code equals},
	 * {@link EqualsVerifier} should be used as well to verify its
	 * adherence to the contracts.
	 * 
	 * @param redefinedSubclass A subclass of T for which no instance can be
	 * 				equal to any instance of T.
	 * @return {@code this}, for easy method chaining.
	 * 
	 * @see Feature#WEAK_INHERITANCE_CHECK
	 */
	public EqualsVerifier<T> withRedefinedSubclass(Class<? extends T> redefinedSubclass) {
		this.redefinedSubclass = redefinedSubclass;
		return this;
	}
	
	/**
	 * Performs the verification of the contracts for {@code equals} and
	 * {@code hashCode}.
	 * 
	 * @throws AssertionError If the contract is not met, or if
	 * 				{@link EqualsVerifier}'s preconditions do not hold.
	 */
	public void verify() {
		try {
			if (CORE_CLASSES.contains(klass) || klass.isEnum()) {
				return;
			}
		
			ensureUnequalExamples();
			
			FieldsChecker<T> fieldsChecker = new FieldsChecker<T>(instantiator, features);
			ExamplesChecker<T> examplesChecker = new ExamplesChecker<T>(instantiator, equalExamples, unequalExamples);
			HierarchyChecker<T> hierarchyChecker = new HierarchyChecker<T>(instantiator, features, redefinedSubclass);
			
			fieldsChecker.checkNull();
			verifyPreconditions();
			examplesChecker.check();
			hierarchyChecker.check();
			
			fieldsChecker.check();
		}
		catch (Throwable e) {
			if (features.contains(Feature.VERBOSE)) {
				e.printStackTrace();
			}
			fail(e.getMessage());
		}
	}
	
	private static <T> List<T> buildList1(T first, T... tail) {
		if (first == null) {
			throw new IllegalArgumentException("First example is null");
		}

		List<T> result = new ArrayList<T>();
		result.add(first);
		addArrayElementsToList(result, tail);
		
		return result;
	}

	private static <T> List<T> buildList2(T first, T second, T... tail) {
		if (first == null) {
			throw new IllegalArgumentException("First example is null");
		}
		if (second == null) {
			throw new IllegalArgumentException("Second example is null");
		}

		List<T> result = new ArrayList<T>();
		result.add(first);
		result.add(second);
		addArrayElementsToList(result, tail);
		
		return result;
	}
	
	private static <T> void addArrayElementsToList(List<T> list, T... tail) {
		if (tail != null) {
			for (T e : tail) {
				if (e == null) {
					throw new IllegalArgumentException("One of the examples is null");
				}
				list.add(e);
			}
		}
	}

	private void verifyPreconditions() {
		assertTrue("Precondition: no examples.", unequalExamples.size() > 0);
		for (T example : equalExamples) {
			assertTrue("Precondition: " + equalExamples.get(0) + " and " + example + " are of different classes",
					klass.isAssignableFrom(example.getClass()));
		}
		for (T example : unequalExamples) {
			assertTrue("Precondition: " + unequalExamples.get(0) + " and " + example + " are of different classes",
					klass.isAssignableFrom(example.getClass()));
		}
	}
	
	private void ensureUnequalExamples() {
		if (unequalExamples.size() > 0) {
			return;
		}
		
		T first = instantiator.instantiate();
		instantiator.scramble(first);
		T second = instantiator.instantiate();
		instantiator.scramble(second);
		instantiator.scramble(second);
		
		unequalExamples.add(first);
		unequalExamples.add(second);
	}

	/**
	 * Helper class for
	 * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
	 * Its purpose is to make sure, at compile time, that a list of unequal
	 * examples is given, as well as the list of equal examples that are
	 * supplied to the aforementioned method.
	 * 
	 * @author Jan Ouwens
	 */
	public static class RelaxedEqualsVerifierHelper<T> {
		private final Class<T> klass;
		private final Instantiator<T> instantiator;
		private final List<T> equalExamples;

		/**
		 * Private constructor, only to be called by
		 * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
		 */
		private RelaxedEqualsVerifierHelper(Class<T> klass, Instantiator<T> instantiator, List<T> examples) {
			this.klass = klass;
			this.instantiator = instantiator;
			this.equalExamples = examples;
		}
		
		/**
		 * Asks for an unequal instance of T and subsequently returns a fully
		 * constructed instance of {@link EqualsVerifier}.
		 * 
		 * @param example An instance of T that is unequal to the previously
		 * 			supplied equal examples.
		 * @return An instance of {@link EqualsVerifier}.
		 */
		public EqualsVerifier<T> andUnequalExample(T example) {
			List<T> unequalExamples = buildList1(example, (T[])null);
			return new EqualsVerifier<T>(klass, instantiator, equalExamples, unequalExamples);
		}

		/**
		 * Asks for a list of unequal instances of T and subsequently returns a
		 * fully constructed instance of {@link EqualsVerifier}.
		 * 
		 * @param first An instance of T that is unequal to the previously
		 * 			supplied equal examples.
		 * @param tail An array of instances of T, all of which are unequal to
		 * 			one	another, to {@code first}, and to the previously
		 * 			supplied equal examples. May also contain instances of
		 * 			subclasses of T.
		 * @return An instance of {@link EqualsVerifier}.
		 */
		public EqualsVerifier<T> andUnequalExamples(T first, T... tail) {
			List<T> unequalExamples = buildList1(first, tail);
			return new EqualsVerifier<T>(klass, instantiator, equalExamples, unequalExamples);
		}
	}
}
