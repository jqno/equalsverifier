/*
 * Copyright 2010-2013, 2015 Jan Ouwens
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

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.MockStaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.PrefabValuesFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class PrefabValuesCreatorTest {
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag ENUM_TAG = new TypeTag(Enum.class);
    private static final TypeTag ONE_ELT_ENUM_TAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag NODE_TAG = new TypeTag(Node.class);
    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MockStaticFieldValueStash stash;
    private PrefabValues prefabValues;

    @Before
    public void setup() {
        stash = new MockStaticFieldValueStash();
        prefabValues = PrefabValuesFactory.withPrimitiveFactories(stash);
    }

    @Test
    public void stashed() {
        prefabValues.giveRed(POINT_TAG);
        assertEquals(Point.class, stash.lastBackuppedType);
    }

    @Test
    public void simple() {
        Point red = prefabValues.giveRed(POINT_TAG);
        Point black = prefabValues.giveBlack(POINT_TAG);
        assertFalse(red.equals(black));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        Point red = prefabValues.giveRed(POINT_TAG);
        Point black = prefabValues.giveBlack(POINT_TAG);

        assertSame(red, prefabValues.giveRed(POINT_TAG));
        assertSame(black, prefabValues.giveBlack(POINT_TAG));
    }

    @Test
    public void createEnum() {
        assertNotNull(prefabValues.giveRed(ENUM_TAG));
        assertNotNull(prefabValues.giveBlack(ENUM_TAG));
    }

    @Test
    public void createOneElementEnum() {
        assertNotNull(prefabValues.giveRed(ONE_ELT_ENUM_TAG));
        assertNotNull(prefabValues.giveBlack(ONE_ELT_ENUM_TAG));
    }

    @Test
    public void createEmptyEnum() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("Enum EmptyEnum has no elements");
        prefabValues.giveRed(new TypeTag(EmptyEnum.class));
    }

    @Test
    public void oneStepRecursiveType() {
        prefabValues.addFactory(Node.class, new Node(), new Node());
        prefabValues.giveRed(NODE_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.giveRed(NODE_TAG);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        prefabValues.addFactory(NodeArray.class, new NodeArray(), new NodeArray());
        prefabValues.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void addTwoStepRecursiveType() {
        prefabValues.addFactory(TwoStepNodeB.class, new TwoStepNodeB(), new TwoStepNodeB());
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        thrown.expect(RecursionException.class);
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        prefabValues.addFactory(TwoStepNodeArrayB.class, new TwoStepNodeArrayB(), new TwoStepNodeArrayB());
        prefabValues.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        thrown.expect(RecursionException.class);
        prefabValues.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        prefabValues.giveRed(new TypeTag(NotRecursiveA.class));
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        thrown.expectMessage(containsString(RecursiveWithAnotherFieldFirst.class.getSimpleName()));
        thrown.expectMessage(not(containsString(RecursiveThisIsTheOtherField.class.getSimpleName())));
        prefabValues.giveRed(new TypeTag(RecursiveWithAnotherFieldFirst.class));
    }

    @Test
    public void exceptionMessage() {
        thrown.expectMessage(TwoStepNodeA.class.getSimpleName());
        thrown.expectMessage(TwoStepNodeB.class.getSimpleName());
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void skipStaticFinal() {
        prefabValues.giveRed(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {
        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
