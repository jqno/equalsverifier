/*
 * Copyright 2013 Jan Ouwens
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
package nl.jqno.equalsverifier.coverage;

import java.util.Arrays;
import java.util.Collection;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CoverageWithInheritanceTest<T, U extends T, V extends U> {
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ HandwrittenCanEqual.class },
				{ LombokCanEqual.class }
		});
	}
	
	private final Class<?> containerType;
	private final Class<T> superType;
	private final Class<U> subType;
	private final Class<V> endpointType;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CoverageWithInheritanceTest(Class<?> containerType) {
		this.containerType = containerType;
		Class[] containingTypes = containerType.getClasses();
		this.superType = find(containingTypes, "Point");
		this.subType = find(containingTypes, "ColorPoint");
		this.endpointType = find(containingTypes, "EndPoint");
	}
	
	@SuppressWarnings("rawtypes")
	private Class find(Class[] types, String name) {
		Class result = null;
		for (Class type : types) {
			if (type.getSimpleName().equals(name)) {
				result = type;
			}
		}
		return result;
	}
	
	@Test
	public void testSuperCoverage() {
		EqualsVerifier.forClass(superType)
				.withRedefinedSubclass(subType)
				.verify();
	}
	
	@Test
	public void testSubCoverage() {
		EqualsVerifier.forClass(subType)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(endpointType)
				.verify();
	}

	@Test
	public void callTheConstructors() throws Exception {
		containerType.getConstructor().newInstance();
		superType.getConstructor(int.class, int.class).newInstance(0, 0);
		subType.getConstructor(int.class, int.class, Color.class).newInstance(0, 0, Color.INDIGO);
		endpointType.getConstructor(int.class, int.class, Color.class).newInstance(0, 0, Color.INDIGO);
	}
}
