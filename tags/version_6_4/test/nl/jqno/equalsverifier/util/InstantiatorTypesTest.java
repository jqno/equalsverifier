/*
 * Copyright 2010 Jan Ouwens
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

import nl.jqno.equalsverifier.util.Instantiator;
import nl.jqno.equalsverifier.util.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.InterfaceContainer;

import org.junit.Test;

public class InstantiatorTypesTest {
	@Test
	public void instantiateTypes() {
		Instantiator<AllTypesContainer> instantiator = Instantiator.forClass(AllTypesContainer.class);
		AllTypesContainer cc = instantiator.instantiate();
		instantiator.scramble(cc);
	}

	@Test
	public void instantiateArrayTypes() {
		Instantiator<AllArrayTypesContainer> instantiator = Instantiator.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer cc = instantiator.instantiate();
		instantiator.scramble(cc);
	}
	
	@Test
	public void interfaceField() {
		Instantiator<InterfaceContainer> instantiator = Instantiator.forClass(InterfaceContainer.class);
		InterfaceContainer ic = new InterfaceContainer();
		instantiator.scramble(ic);
	}
	
	@Test
	public void abstractClassField() {
		Instantiator<AbstractClassContainer> instantiator = Instantiator.forClass(AbstractClassContainer.class);
		AbstractClassContainer ic = new AbstractClassContainer();
		instantiator.scramble(ic);
	}
}
