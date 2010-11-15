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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.points.PointContainer;

import org.junit.Test;

public class ObjectAccessorTest {
	@Test
	public void get() {
		Object foo = new Object();
		ObjectAccessor<Object> accessor = ObjectAccessor.of(foo);
		assertSame(foo, accessor.get());
	}
	
	@Test
	public void fieldAccessorFor() throws NoSuchFieldException {
		PointContainer foo = new PointContainer(new Point(1, 2));
		Field field = PointContainer.class.getDeclaredField("point");
		
		ObjectAccessor<PointContainer> accessor = ObjectAccessor.of(foo);
		FieldAccessor fieldAccessor = accessor.fieldAccessorFor(field);
		
		fieldAccessor.nullField();
		assertNull(foo.getPoint());
	}
}
