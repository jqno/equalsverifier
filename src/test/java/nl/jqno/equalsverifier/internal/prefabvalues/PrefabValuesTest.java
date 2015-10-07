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
package nl.jqno.equalsverifier.internal.prefabvalues;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrefabValuesTest {
    private final static TypeTag STRING_TAG = new TypeTag(String.class);
    private PrefabValues pv = new PrefabValues();

    @Before
    public void setUp() {
        pv.addFactory(String.class, new TestFactory());
    }

    @Test
    public void sanityTestFactoryIncreasesStringLength() {
        TestFactory f = new TestFactory();
        assertEquals("r", f.createRed());
        assertEquals("rr", f.createRed());
        assertEquals("rrr", f.createRed());
    }

    @Test
    public void giveRedFromFactory() {
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    @Test
    public void giveRedFromCache() {
        pv.giveRed(STRING_TAG);
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    private static class TestFactory implements PrefabValueFactory<String> {
        private String red;
        private String black;

        public TestFactory() { red = ""; black = ""; }

        @Override public String createRed() { red += "r"; return red; }
        @Override public String createBlack() { black += "b"; return black; }
    }
}
