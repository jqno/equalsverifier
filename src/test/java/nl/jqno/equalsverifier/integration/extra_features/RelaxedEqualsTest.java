/*
 * Copyright 2009-2010, 2013-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Multiple;
import org.junit.Before;
import org.junit.Test;

public class RelaxedEqualsTest extends IntegrationTestBase {
    private Multiple a;
    private Multiple b;
    private Multiple x;

    @Before
    public void setup() {
        a = new Multiple(1, 2);
        b = new Multiple(2, 1);
        x = new Multiple(2, 2);
    }

    @Test
    public void fail_whenObjectsWithDifferentFieldsAreEqual() {
        expectException(IllegalArgumentException.class, "Two objects are equal to each other");
        EqualsVerifier.forExamples(a, b);
    }

    @Test
    public void succeed_whenObjectsWithDifferentFieldsAreEqual_givenTheyAreGivenAsRelaxedEqualExamples() {
        EqualsVerifier.forRelaxedEqualExamples(a, b)
                .andUnequalExample(x)
                .verify();
    }

    @Test
    public void fail_whenTheSameObjectIsGivenAsAnUnequalExample() {
        expectException(IllegalArgumentException.class, "An equal example also appears as unequal example.");
        EqualsVerifier.forRelaxedEqualExamples(a, b)
                .andUnequalExamples(a);
    }

    @Test
    public void succeed_whenAnUnusedFieldIsNull_givenItIsGivenAsARelaxedEqualExample() {
        EqualsVerifier.forRelaxedEqualExamples(new NullContainingSubMultiple(1, 2), new NullContainingSubMultiple(2, 1))
                .andUnequalExample(new NullContainingSubMultiple(2, 2))
                .verify();
    }

    public class NullContainingSubMultiple extends Multiple {
        @SuppressWarnings("unused")
        private final String noValue = null;

        public NullContainingSubMultiple(int a, int b) {
            super(a, b);
        }
    }
}
