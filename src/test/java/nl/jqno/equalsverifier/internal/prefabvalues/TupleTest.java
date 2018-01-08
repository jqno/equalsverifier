/*
 * Copyright 2015, 2018 Jan Ouwens
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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TupleTest {
    private Tuple<String> tuple = new Tuple<>("red", "black", new String("red"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(Tuple.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void getRed() {
        assertEquals("red", tuple.getRed());
    }

    @Test
    public void getBlack() {
        assertEquals("black", tuple.getBlack());
    }

    @Test
    public void getRedCopy() {
        assertEquals("red", tuple.getRedCopy());
    }

    @Test
    public void redAndRedCopyInvariant() {
        assertEquals(tuple.getRed(), tuple.getRedCopy());
        assertNotSame(tuple.getRed(), tuple.getRedCopy());
    }

    @Test
    public void constructorThrowsWhenRedAndRedCopyAreNotEqual() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("redCopy should equal red");
        new Tuple<>("red", "black", "green");
    }

    @Test
    public void constructorThrowsWhenRedAndRedCopyAreTheSame() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("redCopy should not be the same instance as red");
        new Tuple<>("red", "black", "red");
    }
}
