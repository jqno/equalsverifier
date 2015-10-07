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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PrefabValuesTest {
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private PrefabValues pv = new PrefabValues();

    @Before
    public void setUp() {
        pv.addFactory(String.class, new AppendingStringTestFactory());
        pv.addFactory(int.class, new SimpleFactory<>(42, 1337));
    }

    @Test
    public void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertEquals("r", f.createValues(null, null).getRed());
        assertEquals("rr", f.createValues(null, null).getRed());
        assertEquals("rrr", f.createValues(null, null).getRed());
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

    @Test
    public void stringListIsSeparateFromIntegerList() {
        pv.addFactory(List.class, new ListTestFactory());

        List<String> strings = pv.giveRed(new TypeTag(List.class, new TypeTag(String.class)));
        List<Integer> ints = pv.giveRed(new TypeTag(List.class, new TypeTag(int.class)));

        assertEquals("r", strings.get(0));
        assertEquals(42, (int)ints.get(0));
    }

    private static class AppendingStringTestFactory implements PrefabValueFactory<String> {
        private String red;
        private String black;

        public AppendingStringTestFactory() { red = ""; black = ""; }

        @Override
        public Tuple<String> createValues(TypeTag tag, PrefabValues prefabValues) {
            red += "r"; black += "b";
            return new Tuple<>(red, black);
        }
    }

    private static class ListTestFactory implements PrefabValueFactory<List> {
        @Override
        @SuppressWarnings("unchecked")
        public Tuple<List> createValues(TypeTag tag, PrefabValues prefabValues) {
            TypeTag subtag = tag.getGenericTypes().get(0);

            List red = new ArrayList<>();
            red.add(prefabValues.giveRed(subtag));

            List black = new ArrayList<>();
            black.add(prefabValues.giveBlack(subtag));

            return new Tuple<>(red, black);
        }
    }
}
