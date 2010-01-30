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

/**
 * Enum of warnings that can be suppressed in {@link EqualsVerifier}.
 * 
 * @author Jan Ouwens
 * @see EqualsVerifier#suppress(Warning...)
 */
public enum Warning {
	/**
	 * Disables checks for non-final fields on which {@code equals} and
	 * {@code hashCode} depend.
	 * <p>
	 * {@link EqualsVerifier}'s standard behaviour is to disallow non-final
	 * fields being used in {@code equals} and {@code hashCode} methods, since
	 * classes that depend on non-final fields in these methods cannot reliably
	 * be used in collections.
	 * <p>
	 * However, sometimes an external library requires that fields be
	 * non-final. An example of this are Java Beans. In such a case, this
	 * method can be used to prevent {@link EqualsVerifier} from checking for
	 * non-final fields.
	 */
	NONFINAL_FIELDS,
	
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
	NULL_FIELDS,
	
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
	STRICT_INHERITANCE;
}
