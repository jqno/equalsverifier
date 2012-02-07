/*
 * Copyright 2009-2012 Jan Ouwens
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

import static nl.jqno.equalsverifier.util.Assert.fail;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.InternalException;
import nl.jqno.equalsverifier.util.PrefabValues;
import nl.jqno.equalsverifier.util.JavaApiPrefabValues;

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
 * subclass, or call {@link #suppress(Warning...)} with
 * {@link Warning#STRICT_INHERITANCE} to disable the check.<br>
 * - Call {@link #suppress(Warning...)} to suppress warnings given by
 * {@code EqualsVerifier}.<br>
 * - Call {@link #verify()} to perform the actual verifications.
 * <p>
 * Example use:
 * 
 * <pre>{@code
 * EqualsVerifier.forClass(My.class).verify();
 * }</pre>
 * 
 * Or, if you prefer to use a {@code getClass()} check instead of an
 * {@code instanceof} check in the body of your {@code equals} method:
 * 
 * <pre>{@code
 * EqualsVerifier.forClass(My.class)
 *     .useGetClass()
 *     .verify();
 * }</pre>
 * 
 * With some warnings suppressed:
 * 
 * <pre>{@code
 * EqualsVerifier.forClass(My.class)
 *     .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
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
 * - The finality of the fields in terms of which {@code equals} and
 * {@code hashCode} are defined. (Optional)<br>
 * - The finality of the class under test and of the {@code equals} method
 * itself, when applicable.<br>
 * - That transient fields not be included in the {@code equals} contract for
 * the class. (Optional)
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
	private final Class<T> type;
	private final List<T> equalExamples;
	private final List<T> unequalExamples;
	private final PrefabValues prefabValues;
	private final StaticFieldValueStash stash;
	
	private final EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
	private boolean usingGetClass = false;
	private boolean allFieldsShouldBeUsed = false;
	private boolean hasRedefinedSubclass = false;
	private Class<? extends T> redefinedSubclass = null;
	private boolean verbose = false;
	
	/**
	 * Factory method. For general use.
	 * 
	 * @param type The class for which the {@code equals} method should be
	 * 				tested.
	 */
	public static <T> EqualsVerifier<T> forClass(Class<T> type) {
		List<T> equalExamples = new ArrayList<T>();
		List<T> unequalExamples = new ArrayList<T>();

		return new EqualsVerifier<T>(type, equalExamples, unequalExamples);
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
	 * @param more More instances of T, all of which are unequal to one
	 *		 		another and to {@code first} and {@code second}. May also
	 *				contain instances of subclasses of T.
	 */
	public static <T> EqualsVerifier<T> forExamples(T first, T second, T... more) {
		List<T> equalExamples = new ArrayList<T>();
		List<T> unequalExamples = buildListOfAtLeastTwo(first, second, more);
		
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>)first.getClass();
		
		return new EqualsVerifier<T>(type, equalExamples, unequalExamples);
	}
	
	/**
	 * Factory method. Asks for a list of equal, but not identical, instances
	 * of T.
	 * 
	 * For use when T is a class which has relaxed equality
	 * rules. This happens when two instances of T are equal even though the
	 * its internal state is different.
	 * 
	 * This could happen, for example, in a Rational class that doesn't
	 * normalize: new Rational(1, 2).equals(new Rational(2, 4)) would return
	 * true. 
	 * 
	 * Using this factory method requires that
	 * {@link RelaxedEqualsVerifierHelper#andUnequalExamples(Object, Object...)}
	 * be called to supply a list of unequal instances of T.
	 * 
	 * @param first An instance of T.
	 * @param second Another instance of T, which is equal, but not identical,
	 * 				to {@code first}.
	 * @param more More instances of T, all of which are equal, but not
	 * 				identical, to one another and to {@code first} and
	 * 				{@code second}.
	 */
	public static <T> RelaxedEqualsVerifierHelper<T> forRelaxedEqualExamples(T first, T second, T... more) {
		List<T> examples = buildListOfAtLeastTwo(first, second, more);
		
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>)first.getClass();
		
		return new RelaxedEqualsVerifierHelper<T>(type, examples);
	}

	/**
	 * Private constructor. Call {@link #forClass(Class)},
	 * {@link #forExamples(Object, Object, Object...)} or
	 * {@link #forRelaxedEqualExamples(Object, Object, Object...)} instead.
	 */
	private EqualsVerifier(Class<T> type, List<T> equalExamples, List<T> unequalExamples) {
		this.type = type;
		this.equalExamples = equalExamples;
		this.unequalExamples = unequalExamples;
		
		this.stash = new StaticFieldValueStash();
		this.prefabValues = new PrefabValues(stash);
		JavaApiPrefabValues.addTo(prefabValues);
	}
	
	/**
	 * Suppresses warnings given by {@code EqualsVerifier}. See {@link Warning}
	 * to see what warnings can be suppressed.
	 * 
	 * @param warnings A list of warnings to suppress in
	 * 			{@code EqualsVerifier}.
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> suppress(Warning... warnings) {
		for (Warning warning : warnings) {
			this.warningsToSuppress.add(warning);
		}
		return this;
	}
	
	/**
	 * Adds prefabricated values for instance fields of classes that
	 * EqualsVerifier cannot instantiate by itself.
	 * 
	 * @param <S> The class of the prefabricated values.
	 * @param otherType The class of the prefabricated values.
	 * @param red An instance of {@code S}.
	 * @param black Another instance of {@code S}.
	 * @return {@code this}, for easy method chaining.
	 * @throws NullPointerException If either {@code otherType}, {@code red}
	 * 				or {@code black} is null.
	 * @throws IllegalArgumentException If {@code red} equals {@code black}.
	 */
	public <S> EqualsVerifier<T> withPrefabValues(Class<S> otherType, S red, S black) {
		if (otherType == null) {
			throw new NullPointerException("Type is null");
		}
		if (red == null || black == null) {
			throw new NullPointerException("One or both values are null.");
		}
		if (red.equals(black)) {
			throw new IllegalArgumentException("Both values are equal.");
		}
		prefabValues.put(otherType, red, black);
		return this;
	}
	
	/**
	 * Signals that {@code getClass} is used in the implementation of the
	 * {@code equals} method, instead of an {@code instanceof} check.
	 * 
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> usingGetClass() {
		usingGetClass = true;
		return this;
	}
	
	/**
	 * Signals that all non-transient fields are relevant in the {@code equals}
	 * contract. {@code EqualsVerifier} will fail if one non-transient field
	 * does not affect the outcome of {@code equals}.
	 * 
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> allFieldsShouldBeUsed() {
		allFieldsShouldBeUsed = true;
		return this;
	}
	
	/**
	 * Signals that T is part of an inheritance hierarchy where {@code equals}
	 * is overridden. Call this method if T has overridden {@code equals} and
	 * {@code hashCode}, and one or more of T's superclasses have as well.
	 * <p>
	 * T itself does not necessarily have to have subclasses that redefine
	 * {@code equals} and {@code hashCode}.
	 * 
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> withRedefinedSuperclass() {
		hasRedefinedSubclass = true;
		return this;
	}
	
	/**
	 * Supplies a reference to a subclass of T in which {@code equals} is
	 * overridden. Calling this method is mandatory if {@code equals} is not
	 * final and a strong verification is performed.
	 * 
	 * Note that, for each subclass that overrides {@code equals},
	 * {@link EqualsVerifier} should be used as well to verify its
	 * adherence to the contracts.
	 * 
	 * @param redefinedSubclass A subclass of T for which no instance can be
	 * 				equal to any instance of T.
	 * @return {@code this}, for easy method chaining.
	 * 
	 * @see Warning#STRICT_INHERITANCE
	 */
	public EqualsVerifier<T> withRedefinedSubclass(Class<? extends T> redefinedSubclass) {
		this.redefinedSubclass = redefinedSubclass;
		return this;
	}
	
	/**
	 * Makes {@link EqualsVerifier} more verbose. Every time the
	 * {@link EqualsVerifier#verify()} method notes a failure and throws an
	 * {@link AssertionError}, a stacktrace will also be printed to
	 * {@link System#err}.
	 * 
	 * Primarily intended for debugging purposes.
	 * 
	 * @return {@code this}, for easy method chaining.
	 */
	public EqualsVerifier<T> debug() {
		verbose = true;
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
			stash.backup(type);
			performVerification();
		}
		catch (AssertionError e) {
			handleError(e, false);
		}
		catch (InternalException e) {
			handleError(e, false);
		}
		catch (Throwable e) {
			handleError(e, true);
		}
		finally {
			stash.restoreAll();
		}
	}

	private void handleError(Throwable e, boolean printExceptionName) {
		if (verbose) {
			e.printStackTrace();
		}
		
		String message = "";
		message += printExceptionName ? e.getClass().getName() + ": " : "";
		message += e.getMessage() == null ? "" : e.getMessage();
		message += "\nFor more information, go to: http://code.google.com/p/equalsverifier/wiki/ErrorMessages";
		
		fail(message);
	}

	private void performVerification() {
		ClassAccessor<T> classAccessor = ClassAccessor.of(type, prefabValues, warningsToSuppress.contains(Warning.ANNOTATION));
		verifyWithoutExamples(classAccessor);
		ensureUnequalExamples(classAccessor);
		verifyWithExamples(classAccessor);
	}

	private void verifyWithoutExamples(ClassAccessor<T> classAccessor) {
		Checker signatureChecker = new SignatureChecker<T>(type);
		Checker abstractDelegationChecker = new AbstractDelegationChecker<T>(classAccessor);
		Checker nullChecker = new NullChecker<T>(classAccessor, warningsToSuppress);
		
		signatureChecker.check();
		abstractDelegationChecker.check();
		nullChecker.check();
	}
	
	private void ensureUnequalExamples(ClassAccessor<T> classAccessor) {
		if (unequalExamples.size() > 0) {
			return;
		}
		
		unequalExamples.add(classAccessor.getRedObject());
		unequalExamples.add(classAccessor.getBlackObject());
	}

	private void verifyWithExamples(ClassAccessor<T> classAccessor) {
		Checker preconditionChecker = new PreconditionChecker<T>(type, equalExamples, unequalExamples);
		Checker examplesChecker = new ExamplesChecker<T>(type, equalExamples, unequalExamples);
		Checker hierarchyChecker = new HierarchyChecker<T>(classAccessor, warningsToSuppress, usingGetClass, hasRedefinedSubclass, redefinedSubclass);
		Checker fieldsChecker = new FieldsChecker<T>(classAccessor, warningsToSuppress, allFieldsShouldBeUsed);

		preconditionChecker.check();
		examplesChecker.check();
		hierarchyChecker.check();
		fieldsChecker.check();
	}
	
	private static <T> List<T> buildListOfAtLeastOne(T first, T... more) {
		if (first == null) {
			throw new IllegalArgumentException("First example is null");
		}

		List<T> result = new ArrayList<T>();
		result.add(first);
		addArrayElementsToList(result, more);
		
		return result;
	}

	private static <T> List<T> buildListOfAtLeastTwo(T first, T second, T... more) {
		if (first == null) {
			throw new IllegalArgumentException("First example is null");
		}
		if (second == null) {
			throw new IllegalArgumentException("Second example is null");
		}

		List<T> result = new ArrayList<T>();
		result.add(first);
		result.add(second);
		addArrayElementsToList(result, more);
		
		return result;
	}
	
	private static <T> void addArrayElementsToList(List<T> list, T... more) {
		if (more != null) {
			for (T e : more) {
				if (e == null) {
					throw new IllegalArgumentException("One of the examples is null");
				}
				list.add(e);
			}
		}
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
		private final Class<T> type;
		private final List<T> equalExamples;

		/**
		 * Private constructor, only to be called by
		 * {@link EqualsVerifier#forRelaxedEqualExamples(Object, Object, Object...)}.
		 */
		private RelaxedEqualsVerifierHelper(Class<T> type, List<T> examples) {
			this.type = type;
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
			List<T> unequalExamples = buildListOfAtLeastOne(example, (T[])null);
			return new EqualsVerifier<T>(type, equalExamples, unequalExamples);
		}

		/**
		 * Asks for a list of unequal instances of T and subsequently returns a
		 * fully constructed instance of {@link EqualsVerifier}.
		 * 
		 * @param first An instance of T that is unequal to the previously
		 * 			supplied equal examples.
		 * @param more More instances of T, all of which are unequal to
		 * 			one	another, to {@code first}, and to the previously
		 * 			supplied equal examples. May also contain instances of
		 * 			subclasses of T.
		 * @return An instance of {@link EqualsVerifier}.
		 */
		public EqualsVerifier<T> andUnequalExamples(T first, T... more) {
			List<T> unequalExamples = buildListOfAtLeastOne(first, more);
			return new EqualsVerifier<T>(type, equalExamples, unequalExamples);
		}
	}
}
