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

/**
 * Enum of features that can be applied to {@link EqualsVerifier}.
 * 
 * @see EqualsVerifier#with(Feature...)
 */
public enum Feature {
	/**
	 * Disables checks for mutable fields on which {@code equals} and
	 * {@code hashCode} depend.
	 * <p>
	 * {@link EqualsVerifier}'s standard behaviour is to disallow mutable
	 * fields being used in {@code equals} and {@code hashCode} methods, since
	 * classes that depend on mutable fields in these methods cannot reliably
	 * be used in collections.
	 * <p>
	 * However, sometimes an external library requires that fields be mutable.
	 * A good example of this are Java Beans. In such a case, this method can
	 * be used to prevent {@link EqualsVerifier} from checking for mutable
	 * fields.
	 */
	ALLOW_MUTABLE_FIELDS,
	
	/**
	 * Disables checks for {@link NullPointerException} within {@code equals},
	 * {@code hashCode} and {@code toString} methods.
	 * <p>
	 * Sometimes the constructor of a class makes sure no field can be null.
	 * If this is the case, and if the fields cannot be made null later in the
	 * lifecycle of the class by setters or other methods, the
	 * {@code fieldsAreNeverNull} method can be used to disable the check for
	 * {@link NullPointerException}.
	 */
	FIELDS_ARE_NEVER_NULL,
	
	/**
	 * Signals that T is part of an inheritance hierarchy where {@code equals}
	 * is overridden. Call this method if T has overridden {@code equals} and
	 * {@code hashCode}, and one or more of T's superclasses have as well.
	 * <p>
	 * T itself does not necessarily have to have subclasses that redefine
	 * {@code equals} and {@code hashCode}.
	 */
	REDEFINED_SUPERCLASS,
	
	/**
	 * Disables some of the stricter inheritance tests; use at your own risk!
	 * <p>
	 * {@link EqualsVerifier}'s standard behaviour, if T is not final and
	 * neither are its {@code equals} and {@code hashCode} methods, is to
	 * require a reference to a subclass of T for which no instance can be
	 * equal to any instance of T, to make sure that subclasses that can
	 * redefine {@code equals} or {@code hashCode} don't break the contract.
	 * <p>
	 * Some may find that too strict for their liking; this method disables
	 * that test.
	 * 
	 * @see EqualsVerifier#withRedefinedSubclass(Class)
	 */
	WEAK_INHERITANCE_CHECK;
}
