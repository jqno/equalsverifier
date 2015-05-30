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

import java.util.EnumSet;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;

public class Configuration<T> {
	private final Class<T> type;
	private final PrefabValues prefabValues;
	private final EnumSet<Warning> warningsToSuppress;
	private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
	private final boolean usingGetClass;
	
	private Configuration(Class<T> type, PrefabValues prefabValues, EnumSet<Warning> warningsToSuppress,
			CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean usingGetClass) {
		
		this.type = type;
		this.prefabValues = prefabValues;
		this.warningsToSuppress = warningsToSuppress;
		this.cachedHashCodeInitializer = cachedHashCodeInitializer;
		this.usingGetClass = usingGetClass;
	}
	
	public static <T> Configuration<T> of(Class<T> type) {
		return new Configuration<T>(type, new PrefabValues(), EnumSet.noneOf(Warning.class), CachedHashCodeInitializer.<T>passthrough(), false);
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public PrefabValues getPrefabValues() {
		return prefabValues;
	}
	
	public Configuration<T> withWarningsToSuppress(EnumSet<Warning> value) {
		return new Configuration<T>(type, prefabValues, value, cachedHashCodeInitializer, usingGetClass);
	}
	
	public EnumSet<Warning> getWarningsToSuppress() {
		return EnumSet.copyOf(warningsToSuppress);
	}
	
	public Configuration<T> withCachedHashCodeInitializer(CachedHashCodeInitializer<T> value) {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, value, usingGetClass);
	}
	
	public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
		return cachedHashCodeInitializer;
	}
	
	public Configuration<T> withUsingGetClass() {
		return new Configuration<T>(type, prefabValues, warningsToSuppress, cachedHashCodeInitializer, true);
	}
	
	public boolean isUsingGetClass() {
		return usingGetClass;
	}
	
	public ClassAccessor<T> createClassAccessor() {
		return ClassAccessor.of(type, prefabValues, warningsToSuppress.contains(Warning.ANNOTATION));
	}
}
