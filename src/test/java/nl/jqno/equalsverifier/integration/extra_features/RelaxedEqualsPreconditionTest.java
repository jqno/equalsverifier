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
import org.junit.Ignore;
import org.junit.Test;

public class RelaxedEqualsPreconditionTest extends IntegrationTestBase {
    private static final String PRECONDITION = "Precondition";
    private static final String DIFFERENT_CLASSES = "are of different classes";
    private static final String TWO_IDENTICAL_OBJECTS_APPEAR = "two identical objects appear";
    private static final String EQUAL_IS_UNEQUAL = "An equal example also appears as unequal example.";
    private static final String OBJECT_APPEARS_TWICE = "the same object appears twice";
    private static final String TWO_OBJECTS_ARE_EQUAL = "Two objects are equal to each other";

    private Multiple red;
    private Multiple black;
    private Multiple green;

    @Before
    public void setup() {
        red = new Multiple(1, 2);
        black = new Multiple(2, 1);
        green = new Multiple(2, 2);
    }

    @Test
    public void throw_whenTheFirstExampleIsNull() {
        expectException(IllegalArgumentException.class, "First example is null.");
        EqualsVerifier.forRelaxedEqualExamples(null, black);
    }

    @Test
    public void throw_whenTheSecondExampleIsNull() {
        expectException(IllegalArgumentException.class, "Second example is null.");
        EqualsVerifier.forRelaxedEqualExamples(red, null);
    }

    @Test@Ignore("TODO: how should this interact with allFieldsShouldBeUsed?")
    public void succeed_whenTheVarargArrayIsNull() {
        EqualsVerifier.forRelaxedEqualExamples(red, black, (Multiple[])null)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenAVarargParameterIsNull() {
        expectException(IllegalArgumentException.class, "One of the examples is null.");
        Multiple another = new Multiple(-1, -2);
        EqualsVerifier.forRelaxedEqualExamples(red, black, another, null);
    }

    @Test
    public void fail_whenTheUnequalExampleIsNull() {
        expectException(IllegalArgumentException.class, "First example is null.");
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExample(null);
    }

    @Test@Ignore("TODO: how should this interact with allFieldsShouldBeUsed?")
    public void succeed_whenTheUnequalVarargArrayIsNull() {
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, (Multiple[])null)
                .verify();
    }

    @Test
    public void fail_whenAnUnequalVarargParameterIsNull() {
        expectException(IllegalArgumentException.class);
        Multiple another = new Multiple(3, 3);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, another, null);
    }

    @Test
    public void fail_whenEqualExamplesAreOfDifferentRuntimeTypes() {
        expectFailure(PRECONDITION, DIFFERENT_CLASSES, SubMultiple.class.getSimpleName(), Multiple.class.getSimpleName());
        SubMultiple sm = new SubMultiple(1, 2);
        EqualsVerifier.forRelaxedEqualExamples(sm, red)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenTheSameExampleIsGivenTwice() {
        expectFailure(PRECONDITION, OBJECT_APPEARS_TWICE, Multiple.class.getSimpleName());
        EqualsVerifier.forRelaxedEqualExamples(red, red)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenTwoExamplesAreEqual() {
        expectFailure(PRECONDITION, TWO_IDENTICAL_OBJECTS_APPEAR, Multiple.class.getSimpleName());
        Multiple aa = new Multiple(1, 2);
        EqualsVerifier.forRelaxedEqualExamples(red, aa)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenAnEqualExampleIsAlsoGivenAsAnUnequalExample() {
        expectException(IllegalArgumentException.class, EQUAL_IS_UNEQUAL);
        EqualsVerifier.forRelaxedEqualExamples(red, green)
                .andUnequalExample(green);
    }

    @Test
    public void fail_whenTheSameUnequalExampleIsGivenTwice() {
        expectException(IllegalArgumentException.class, TWO_OBJECTS_ARE_EQUAL);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, green);
    }

    @Test
    public void fail_whenTwoUnequalExamplesAreEqualToEachOther() {
        expectException(IllegalArgumentException.class, TWO_OBJECTS_ARE_EQUAL);
        Multiple xx = new Multiple(2, 2);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, xx);
    }

    public static class SubMultiple extends Multiple {
        public SubMultiple(int a, int b) {
            super(a, b);
        }
    }
}
