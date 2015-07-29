/*
 * Copyright 2010, 2012-2013, 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import nl.jqno.equalsverifier.testhelpers.MockStaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Interface;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PrefabValuesTest {
    private static final Class<String> EXISTING_KEY = String.class;
    private static final String EXISTING_RED_VALUE = "the red";
    private static final String EXISTING_BLACK_VALUE = "the black";
    private static final String NON_EXISTING_VALUE = "the unknown";

    private static final Class<Object> NON_EXISTING_KEY = Object.class;
    private static final Object VALUE_FOR_NON_EXISTING_KEY = new Object();

    private MockStaticFieldValueStash stash;
    private PrefabValues p;

    @Before
    public void setup() {
        stash = new MockStaticFieldValueStash();
        p = new PrefabValues(stash);
        p.put(EXISTING_KEY, EXISTING_RED_VALUE, EXISTING_BLACK_VALUE);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void happyPath() {
        assertPrefabValues(p, EXISTING_KEY);
    }

    @Test
    public void backupDelegatesToStash() {
        p.backupToStash(String.class);
        assertEquals(String.class, stash.lastBackuppedType);
    }

    @Test
    public void restoreDelegatesToStash() {
        p.restoreFromStash();
        assertTrue(stash.restoreCalled);
    }

    @Test
    public void putEqualValuesIsAllowedForSingletonFields() {
        p.put(String.class, "a", "a");
    }

    @Test
    public void putAll() {
        PrefabValues q = new PrefabValues(null);
        q.putAll(p);
        assertPrefabValues(q, EXISTING_KEY);
    }

    @Test
    public void contains() {
        assertTrue(p.contains(EXISTING_KEY));
        assertFalse(p.contains(NON_EXISTING_KEY));
    }

    @Test
    public void getRed() {
        assertEquals(EXISTING_RED_VALUE, p.getRed(EXISTING_KEY));
    }

    @Test
    public void getBlack() {
        assertEquals(EXISTING_BLACK_VALUE, p.getBlack(EXISTING_KEY));
    }

    @Test
    public void overwriteKey() {
        p.put(EXISTING_KEY, "another red one", "another black one");
        assertEquals("another red one", p.getRed(EXISTING_KEY));
        assertEquals("another black one", p.getBlack(EXISTING_KEY));
    }

    @Test
    public void getOtherWhenOneExists() {
        assertEquals(EXISTING_BLACK_VALUE, p.getOther(EXISTING_KEY, EXISTING_RED_VALUE));
        assertEquals(EXISTING_RED_VALUE, p.getOther(EXISTING_KEY, EXISTING_BLACK_VALUE));
    }

    @Test
    public void getOtherWhenOneDoesntExist() {
        assertEquals(EXISTING_RED_VALUE, p.getOther(EXISTING_KEY, NON_EXISTING_VALUE));
    }

    @Test
    public void getOtherWhenClassDoesntExist() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("No prefab values for class java.lang.Object exist.");

        p.getOther(NON_EXISTING_KEY, VALUE_FOR_NON_EXISTING_KEY);
    }

    @Test
    public void getOtherWhenClassIsNull() {
        thrown.expect(ReflectionException.class);

        p.getOther(null, VALUE_FOR_NON_EXISTING_KEY);
    }

    @Test
    public void getOtherWhenValueIsNull() {
        assertEquals(EXISTING_RED_VALUE, p.getOther(EXISTING_KEY, null));
    }

    @Test
    public void getOtherWhenClassDoesntMatchValue() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("Type does not match value.");

        p.getOther(String.class, 1);
    }

    @Test
    public void getOtherWhenValueIsPrimitive() {
        p.put(int.class, 1, 2);
        assertEquals(2, p.getOther(int.class, 1));
    }

    @Test
    public void getOtherWhenValueIsSubclassOfSpecifiedClass() {
        p.put(Interface.class, new Interface(){}, new Interface(){});
        assertPrefabValues(p, Interface.class);
    }

    private static void assertPrefabValues(PrefabValues p, Class<?> type) {
        Object red = p.getOther(type, null);
        assertNotNull(red);

        Object black = p.getOther(type, red);
        assertNotNull(black);

        assertFalse(red.equals(black));
        assertEquals(red, p.getOther(type, black));
    }
}
