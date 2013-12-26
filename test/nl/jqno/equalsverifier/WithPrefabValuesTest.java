/*
 * Copyright 2012-2013 Jan Ouwens
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WithPrefabValuesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void nullPointerExceptionOtherClass() {
		thrown.expect(NullPointerException.class);
		
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(null, "red", "black");
	}

	@Test
	public void nullPointerExceptionRed() {
		thrown.expect(NullPointerException.class);
		
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, null, "black");
	}

	@Test
	public void nullPointerExceptionBlack() {
		thrown.expect(NullPointerException.class);
		
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, "red", null);
	}
	
	@Test
	public void illegalArgumentExceptionEqualParameters() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Both values are equal.");
		
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, "red", "red");
	}
}
