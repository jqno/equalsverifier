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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.coverage.LombokCanEqualPointContainer.LombokCanEqualColorPoint;
import nl.jqno.equalsverifier.coverage.LombokCanEqualPointContainer.LombokCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CoverageWithInheritanceTest<T, U extends T> {
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ LombokCanEqualPoint.class, LombokCanEqualColorPoint.class }
		});
	}
	
	private final Class<T> superType;
	private final Class<U> subType;
	
	public CoverageWithInheritanceTest(Class<T> superType, Class<U> subType) {
		this.superType = superType;
		this.subType = subType;
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
				.verify();
	}

	@Test
	public void callTheSuperConstructor() throws Exception {
		Constructor<?> constructor = superType.getConstructor(int.class, int.class);
		constructor.newInstance(0, 0);
	}
	
	@Test
	public void callTheSubConstructor() throws Exception {
		Constructor<?> constructor = subType.getConstructor(int.class, int.class, Color.class);
		constructor.newInstance(0, 0, Color.INDIGO);
	}
}
