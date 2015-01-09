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
package nl.jqno.equalsverifier.util.exceptions;

import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.testhelpers.types.Point;

import org.junit.Test;

public class RecursionExceptionTest {
	@Test
	public void messageContainsAllTypes() {
		LinkedHashSet<Class<?>> stack = new LinkedHashSet<Class<?>>();
		stack.add(String.class);
		stack.add(Point.class);
		stack.add(Object.class);
		
		String message = new RecursionException(stack).getMessage();
		
		for (Class<?> type : stack) {
			assertTrue(message.contains(type.getName()));
		}
	}
}
