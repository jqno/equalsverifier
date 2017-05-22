/*
 * Copyright 2013-2017 Jan Ouwens
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

import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.internal.ClassAccessor;
import nl.jqno.equalsverifier.internal.FieldAccessor;
import nl.jqno.equalsverifier.internal.ObjectAccessor;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.PrefabValuesFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FieldInspectorTest {
    private final PrefabValues prefabValues = PrefabValuesFactory.withPrimitiveFactories();
    private final ClassAccessor<Point> accessor = ClassAccessor.of(Point.class, prefabValues, new HashSet<String>(), false);

    @Test
    public void objectsAreReset_whenEachIterationBegins() {
        FieldInspector<Point> inspector = new FieldInspector<>(accessor, TypeTag.NULL);

        inspector.check(new ResetObjectForEachIterationCheck());
    }

    @Test
    public void objectsAreReset_whenEachIterationBegins_givenNullObjects() {
        FieldInspector<Point> inspector = new FieldInspector<>(accessor, TypeTag.NULL);
        Set<String> nullFields = new HashSet<>();

        inspector.checkWithNull(nullFields, new ResetObjectForEachIterationCheck());
    }

    private final class ResetObjectForEachIterationCheck implements FieldCheck {
        private Object originalReference;
        private Object originalChanged;

        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            if (originalReference == null) {
                originalReference = ObjectAccessor.of(referenceAccessor.getObject()).copy();
                originalChanged = ObjectAccessor.of(changedAccessor.getObject()).copy();
            }
            else {
                assertEquals(originalReference, referenceAccessor.getObject());
                assertEquals(originalChanged, changedAccessor.getObject());
            }

            referenceAccessor.changeField(prefabValues, TypeTag.NULL);
            changedAccessor.changeField(prefabValues, TypeTag.NULL);
        }
    }
}
