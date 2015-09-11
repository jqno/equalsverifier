/*
 * Copyright 2012-2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import org.junit.Test;

public class WithPrefabValuesTest extends IntegrationTestBase {
    @Test
    public void succeed_whenPrefabValuesAreOfSameTypeAsClassUnderTest() {
        EqualsVerifier.forClass(FinalPoint.class)
                .withPrefabValues(FinalPoint.class, new FinalPoint(1, 2), new FinalPoint(2, 3))
                .verify();
    }

    @Test
    public void succeed_whenTheClassIsAlreadyKnown() {
        EqualsVerifier.forClass(FinalPoint.class)
                .withPrefabValues(Object.class, new Object(), new Object());
    }

    @Test
    public void throw_whenTypeIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(null, "red", "black");
    }

    @Test
    public void throw_whenFirstPrefabValueIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(String.class, null, "black");
    }

    @Test
    public void throw_whenSecondPrefabValueIsNull() {
        thrown.expect(NullPointerException.class);

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(String.class, "red", null);
    }

    @Test
    public void throw_whenThePrefabValuesAreTheSame() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Both values are equal.");

        String red = "red";

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(String.class, red, red);
    }

    @Test
    public void throw_whenThePrefabValuesAreEqual() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Both values are equal.");

        String red1 = new String("red");
        String red2 = new String("red");

        EqualsVerifier.forClass(WithPrefabValuesTest.class)
                .withPrefabValues(String.class, red1, red2);
    }
}
