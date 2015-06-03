/*
 * Copyright 2015 Jan Ouwens
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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;

public class Configuration<T> {
	private final Class<T> type;
	private final PrefabValues prefabValues;
	private final EnumSet<Warning> warningsToSuppress;
	private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
	private final boolean usingGetClass;
	private final boolean allFieldsShouldBeUsed;
	private final Set<String> allFieldsShouldBeUsedExceptions;
	
	private Configuration(Class<T> type, PrefabValues prefabValues, EnumSet<Warning> warningsToSuppress,
			CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean usingGetClass,
			boolean allFieldsShouldBeUsed, Set<String> allFieldsShouldBeUsedExceptions) {
		
		this.type = type;
		this.prefabValues = prefabValues;
		this.warningsToSuppress = warningsToSuppress;
		this.cachedHashCodeInitializer = cachedHashCodeInitializer;
		this.usingGetClass = usingGetClass;
		this.allFieldsShouldBeUsed = allFieldsShouldBeUsed;
		this.allFieldsShouldBeUsedExceptions = allFieldsShouldBeUsedExceptions;
	}
	
	public static <T> Configuration<T> of(Class<T> type) {
		return new Configuration<T>(type, new PrefabValues(), EnumSet.noneOf(Warning.class),
				CachedHashCodeInitializer.<T>passthrough(), false, false, new HashSet<String>());
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public PrefabValues getPrefabValues() {
		return prefabValues;
	}
	
	public Configuration<T> withWarningsToSuppress(EnumSet<Warning> value) {
		return new Configuration<T>(type, prefabValues, value, cachedHashCodeInitializer, usingGetClass,
				allFieldsShouldBeUsed, allFieldsShouldBeUsedExceptions);
	}
	
	public EnumSet<Warning> getWarningsToSuppress() {
		return EnumSet.copyOf(warningsToSuppress);
	}
	
	public Configuration<T> withCachedHashCodeInitializer(CachedHashCodeInitializer<T> value) {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, value, usingGetClass,
				allFieldsShouldBeUsed, allFieldsShouldBeUsedExceptions);
	}
	
	public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
		return cachedHashCodeInitializer;
	}
	
	public Configuration<T> withUsingGetClass() {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, cachedHashCodeInitializer, true,
				allFieldsShouldBeUsed, allFieldsShouldBeUsedExceptions);
	}

	public boolean isUsingGetClass() {
		return usingGetClass;
	}

	public Configuration<T> withAllFieldsShouldBeUsed() {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, cachedHashCodeInitializer, usingGetClass,
				true, allFieldsShouldBeUsedExceptions);
	}

	public boolean isAllFieldsShouldBeUsed() {
		return allFieldsShouldBeUsed;
	}

	public Configuration<T> withAllFieldsShouldBeUsedExceptions(String[] value) {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, cachedHashCodeInitializer, usingGetClass,
				allFieldsShouldBeUsed, new HashSet<String>(Arrays.asList(value)));
	}

	public Set<String> getAllFieldsShouldBeUsedExceptions() {
		return Collections.unmodifiableSet(allFieldsShouldBeUsedExceptions);
	}

	public ClassAccessor<T> createClassAccessor() {
		return ClassAccessor.of(type, prefabValues, warningsToSuppress.contains(Warning.ANNOTATION));
	}
}
