/*
 * Copyright 2013-2014 Jan Ouwens
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

import static junit.framework.Assert.assertEquals;
import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.testhelpers.points.Point;
import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.ObjectAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;

import org.junit.Test;

public class FieldInspectorTest {
	private final PrefabValues prefabValues = new PrefabValues(new StaticFieldValueStash());
	private final ClassAccessor<Point> accessor = ClassAccessor.of(Point.class, prefabValues, false);
	
	@Test
	public void objectsAreReset_whenEachIterationBegins() {
		FieldInspector<Point> inspector = new FieldInspector<Point>(accessor);
		
		inspector.check(new ResetObjectForEachIterationCheck());
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
			
			referenceAccessor.changeField(prefabValues);
			changedAccessor.changeField(prefabValues);
		}
	}
}
