/*
 * Copyright 2010, 2015-2016 Jan Ouwens
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Signals that a recursion has been detected while traversing the fields of a
 * data structure.
 *
 * @author Jan Ouwens
 */
@SuppressWarnings("serial")
@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class RecursionException extends MessagingException {
    private final LinkedHashSet<TypeTag> typeStack;

    /**
     * Constructor.
     *
     * @param typeStack A collection of types that have been encountered prior
     *          to detecting the recursion.
     */
    public RecursionException(LinkedHashSet<TypeTag> typeStack) {
        super();
        this.typeStack = typeStack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recursive datastructure.\nAdd prefab values for one of the following types: ");
        Iterator<TypeTag> i = typeStack.iterator();
        sb.append(i.next().toString());
        while(i.hasNext()) {
            sb.append(", ");
            sb.append(i.next().toString());
        }
        sb.append(".");
        return sb.toString();
    }
}
