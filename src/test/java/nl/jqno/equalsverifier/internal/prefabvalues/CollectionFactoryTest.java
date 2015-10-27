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

import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag.Wildcard;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class CollectionFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGLIST_TYPETAG = new TypeTag(List.class, STRING_TYPETAG);
    private static final TypeTag STRINGSET_TYPETAG = new TypeTag(Set.class, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARD_TYPETAG = new TypeTag(Wildcard.class);
    private static final TypeTag WILDCARDLIST_TYPETAG = new TypeTag(List.class, WILDCARD_TYPETAG);
    private static final TypeTag RAWLIST_TYPETAG = new TypeTag(List.class);

    private static final CollectionFactory<List> LIST_FACTORY = new StubListPrefabValueFactory();
    private static final CollectionFactory<Set> SET_FACTORY = new StubSetPrefabValueFactory();

    private final PrefabValues prefabValues = new PrefabValues(new StaticFieldValueStash());
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.giveRed(STRING_TYPETAG);
        black = prefabValues.giveBlack(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blackObject = prefabValues.giveBlack(OBJECT_TYPETAG);
    }

    @Test
    public void createListsOfString() {
        Tuple<List> tuple = LIST_FACTORY.createValues(STRINGLIST_TYPETAG, prefabValues);
        assertEquals(listOf(red), tuple.getRed());
        assertEquals(listOf(black), tuple.getBlack());
    }

    @Test
    public void createSetsOfString() {
        Tuple<Set> tuple = SET_FACTORY.createValues(STRINGSET_TYPETAG, prefabValues);
        assertEquals(setOf(red), tuple.getRed());
        assertEquals(setOf(black), tuple.getBlack());
    }

    @Test
    public void createListsOfWildcard() {
        Tuple<List> tuple = LIST_FACTORY.createValues(WILDCARDLIST_TYPETAG, prefabValues);
        assertEquals(listOf(redObject), tuple.getRed());
        assertEquals(listOf(blackObject), tuple.getBlack());
    }

    @Test
    public void createRawLists() {
        Tuple<List> tuple = LIST_FACTORY.createValues(RAWLIST_TYPETAG, prefabValues);
        assertEquals(listOf(redObject), tuple.getRed());
        assertEquals(listOf(blackObject), tuple.getBlack());
    }

    private static class StubListPrefabValueFactory extends CollectionFactory<List> {
        @Override
        public List createEmpty() {
            return new ArrayList<>();
        }
    }

    private static class StubSetPrefabValueFactory extends CollectionFactory<Set> {
        @Override
        public Set createEmpty() {
            return new HashSet<>();
        }
    }

    private <T> List<T> listOf(T... values) {
        List<T> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }

    private <T> Set<T> setOf(T... values) {
        Set<T> result = new HashSet<>();
        Collections.addAll(result, values);
        return result;
    }
}

