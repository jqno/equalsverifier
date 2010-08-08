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

import nl.jqno.equalsverifier.util.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllRecursiveCollectionImplementationsContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.util.TypeHelper.RecursiveApiClassesContainer;

import org.junit.Test;

public class InstantiatorTypesTest {
	@Test
	public void instantiateTypes() {
		InstantiatorFacade<AllTypesContainer> instantiator = InstantiatorFacade.forClass(AllTypesContainer.class);
		AllTypesContainer cc = instantiator.instantiate();
		instantiator.scramble(cc);
	}

	@Test
	public void instantiateArrayTypes() {
		InstantiatorFacade<AllArrayTypesContainer> instantiator = InstantiatorFacade.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer cc = instantiator.instantiate();
		instantiator.scramble(cc);
	}
	
	@Test
	public void instantiateRecursiveApiTypes() {
		InstantiatorFacade<RecursiveApiClassesContainer> instantiator = InstantiatorFacade.forClass(RecursiveApiClassesContainer.class);
		RecursiveApiClassesContainer racc = instantiator.instantiate();
		instantiator.scramble(racc);
	}
	
	@Test
	public void instantiateMapImplementations() {
		InstantiatorFacade<AllRecursiveCollectionImplementationsContainer> instantiator = InstantiatorFacade.forClass(AllRecursiveCollectionImplementationsContainer.class);
		AllRecursiveCollectionImplementationsContainer arcic = instantiator.instantiate();
		instantiator.scramble(arcic);
	}
	
	@Test
	public void interfaceField() {
		InstantiatorFacade<InterfaceContainer> instantiator = InstantiatorFacade.forClass(InterfaceContainer.class);
		InterfaceContainer ic = new InterfaceContainer();
		instantiator.scramble(ic);
	}
	
	@Test
	public void abstractClassField() {
		InstantiatorFacade<AbstractClassContainer> instantiator = InstantiatorFacade.forClass(AbstractClassContainer.class);
		AbstractClassContainer ic = new AbstractClassContainer();
		instantiator.scramble(ic);
	}
}
