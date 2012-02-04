/*
 * Copyright 2012 Jan Ouwens
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.FieldIterable;
import nl.jqno.equalsverifier.util.ObjectAccessor;

public class StaticFieldValueStash<T> {
	private final Class<T> type;
	private final ObjectAccessor<T> objectAccessor;
	private final Map<Field, Object> stash = new HashMap<Field, Object>();

	public StaticFieldValueStash(Class<T> type) {
		this.type = type;
		this.objectAccessor = ObjectAccessor.of(null, type);
	}
	
	public void backup() {
		for (Field field : new FieldIterable(type)) {
			FieldAccessor accessor = objectAccessor.fieldAccessorFor(field);
			if (accessor.fieldIsStatic()) {
				stash.put(field, accessor.get());
			}
		}
	}
	
	public void restore() {
		for (Field field : new FieldIterable(type)) {
			FieldAccessor accessor = objectAccessor.fieldAccessorFor(field);
			if (accessor.fieldIsStatic()) {
				accessor.set(stash.get(field));
			}
		}
	}
}
