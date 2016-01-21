/*
 * Copyright 2010, 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertTrue;

public class RecursionExceptionTest {
    @Test
    public void messageContainsAllTypes() {
        LinkedHashSet<TypeTag> stack = new LinkedHashSet<>();
        stack.add(new TypeTag(String.class));
        stack.add(new TypeTag(Point.class));
        stack.add(new TypeTag(Object.class));

        String message = new RecursionException(stack).getMessage();

        for (TypeTag tag : stack) {
            assertTrue(message.contains(tag.toString()));
        }
    }
}
