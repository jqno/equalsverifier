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

import org.junit.Test;

public class WithPrefabValuesTest {
	@Test(expected=NullPointerException.class)
	public void nullPointerExceptionOtherClass() {
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(null, "red", "black");
	}

	@Test(expected=NullPointerException.class)
	public void nullPointerExceptionRed() {
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, null, "black");
	}

	@Test(expected=NullPointerException.class)
	public void nullPointerExceptionBlack() {
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, "red", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void illegalArgumentExceptionEqualParameters() {
		EqualsVerifier.forClass(WithPrefabValuesTest.class)
				.withPrefabValues(String.class, "red", "red");
	}
}
