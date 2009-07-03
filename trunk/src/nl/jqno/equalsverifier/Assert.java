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
 * Alternative for org.junit.Assert, so we can assert things but not have a
 * dependency on JUnit.
 */
public class Assert {
	/**
	 * Asserts that two ints are equal to one another. Does nothing if they
	 * are; throws an AssertionError if they're not.
	 * 
	 * @param message Message to be included in the {@link AssertionError}.
	 * @param expected Expected value.
	 * @param actual Actual value.
	 * @throws AssertionError If {@code expected} and {@code actual} are not
	 * 				equal.
	 */
	public static void assertEquals(String message, int expected, int actual) {
		if (expected != actual) {
			throw new AssertionError(message);
		}
	}
	
	/**
	 * Asserts that two Objects are equal to one another. Does nothing if they
	 * are; throws an AssertionError if they're not.
	 * 
	 * @param message Message to be included in the {@link AssertionError}.
	 * @param expected Expected value.
	 * @param actual Actual value.
	 * @throws AssertionError If {@code expected} and {@code actual} are not
	 * 				equal.
	 */
	public static void assertEquals(String message, Object expected, Object actual) {
		if (!expected.equals(actual)) {
			throw new AssertionError(message);
		}
	}
	
	/**
	 * Asserts that an assertion is true. Does nothing if it is; throws an
	 * AssertionError if it isn't.
	 * 
	 * @param message Message to be included in the {@link AssertionError}.
	 * @param assertion Assertion that must be true.
	 * @throws AssertionError If {@code assertion} is false.
	 */
	public static void assertFalse(String message, boolean assertion) {
		if (assertion) {
			throw new AssertionError(message);
		}
	}
	
	/**
	 * Asserts that an assertion is false. Does nothing if it is; throws an
	 * AssertionError if it isn't.
	 * 
	 * @param message Message to be included in the {@link AssertionError}.
	 * @param assertion Assertion that must be true.
	 * @throws AssertionError If {@code assertion} is false.
	 */
	public static void assertTrue(String message, boolean assertion) {
		if (!assertion) {
			throw new AssertionError(message);
		}
	}
	
	/**
	 * Throws an AssertionError.
	 * 
	 * @param message Message to be included in the {@link AssertionError}.
	 * @throws AssertionError Always.
	 */
	public static void fail(String message) {
		throw new AssertionError(message);
	}
}
