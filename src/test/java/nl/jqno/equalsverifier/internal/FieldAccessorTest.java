/*
 * Copyright 2010, 2012, 2014-2015 Jan Ouwens
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

import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Outer.Inner;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FieldAccessorTest {
    private static final Point RED_NEW_POINT = new Point(10, 20);
    private static final Point BLACK_NEW_POINT = new Point(20, 10);
    private static final String FIELD_NAME = "field";

    private PrefabValues prefabValues;

    @Before
    public void setup() {
        prefabValues = new PrefabValues(new StaticFieldValueStash());
        JavaApiPrefabValues.addTo(prefabValues);
    }

    @Test
    public void getObject() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertSame(foo, fieldAccessor.getObject());
    }

    @Test
    public void getField() throws NoSuchFieldException {
        ObjectContainer foo = new ObjectContainer();
        Field field = foo.getClass().getDeclaredField(FIELD_NAME);
        FieldAccessor fieldAccessor = new FieldAccessor(foo, field);
        assertSame(field, fieldAccessor.getField());
    }

    @Test
    public void getFieldType() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertEquals(Object.class, fieldAccessor.getFieldType());
    }

    @Test
    public void getFieldName() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertEquals(FIELD_NAME, fieldAccessor.getFieldName());
    }

    @Test
    public void isNotPrimitive() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertFalse(fieldAccessor.fieldIsPrimitive());
    }

    @Test
    public void isPrimitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertTrue(fieldAccessor.fieldIsPrimitive());
    }

    @Test
    public void isNotFinal() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertFalse(fieldAccessor.fieldIsFinal());
    }

    @Test
    public void isFinal() {
        FinalContainer foo = new FinalContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertTrue(fieldAccessor.fieldIsFinal());
    }

    @Test
    public void isNotStatic() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertFalse(fieldAccessor.fieldIsStatic());
    }

    @Test
    public void isStatic() {
        StaticContainer foo = new StaticContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertTrue(fieldAccessor.fieldIsStatic());
    }

    @Test
    public void isNotTransient() {
        ObjectContainer foo = new ObjectContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertFalse(fieldAccessor.fieldIsTransient());
    }

    @Test
    public void isTransient() {
        TransientContainer foo = new TransientContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertTrue(fieldAccessor.fieldIsTransient());
    }

    @Test
    public void isNotEnum() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
        assertFalse(fieldAccessor.fieldIsSingleValueEnum());
    }

    @Test
    public void isEnumButNotSingleValue() {
        EnumContainer foo = new EnumContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, "twoElementEnum");
        assertFalse(fieldAccessor.fieldIsSingleValueEnum());
    }

    @Test
    public void isSingleValueEnum() {
        EnumContainer foo = new EnumContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, "oneElementEnum");
        assertTrue(fieldAccessor.fieldIsSingleValueEnum());
    }

    @Test
    public void getValuePrimitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        foo.field = 10;
        Object value = getValue(foo, "field");
        assertEquals(10, value);
    }

    @Test
    public void getValueObject() {
        Object object = new Object();
        ObjectContainer foo = new ObjectContainer();
        foo.field = object;
        Object value = getValue(foo, FIELD_NAME);
        assertEquals(object, value);
    }

    @Test
    public void getPrivateValue() {
        PrivateObjectContainer foo = new PrivateObjectContainer();
        getValue(foo, FIELD_NAME);
    }

    @Test
    public void setValuePrimitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        setField(foo, FIELD_NAME, 20);
        assertEquals(20, foo.field);
    }

    @Test
    public void setValueObject() {
        Object object = new Object();
        ObjectContainer foo = new ObjectContainer();
        setField(foo, FIELD_NAME, object);
        assertEquals(object, foo.field);
    }

    @Test
    public void defaultFieldOnObjectSetsNull() throws NoSuchFieldException {
        ObjectContainer foo = new ObjectContainer();
        foo.field = new Object();
        doNullField(foo, FIELD_NAME);
        assertNull(foo.field);
    }

    @Test
    public void defaultFieldOnArraySetsNull() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._array = new int[] { 1, 2, 3 };
        doNullField(foo, "_array");
        assertNull(foo._array);
    }

    @Test
    public void defaultFieldOnBooleanSetsFalse() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._boolean = true;
        doNullField(foo, "_boolean");
        assertEquals(false, foo._boolean);
    }

    @Test
    public void defaultFieldOnByteSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._byte = 10;
        doNullField(foo, "_byte");
        assertEquals(0, foo._byte);
    }

    @Test
    public void defaultFieldOnCharSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._char = 'a';
        doNullField(foo, "_char");
        assertEquals('\u0000', foo._char);
    }

    @Test
    public void defaultFieldOnDoubleSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._double = 1.1;
        doNullField(foo, "_double");
        assertEquals(0.0, foo._double, 0.0000001);
    }

    @Test
    public void defaultFieldOnFloatSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._float = 1.1f;
        doNullField(foo, "_float");
        assertEquals(0.0f, foo._float, 0.0000001);
    }

    @Test
    public void defaultFieldOnIntSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._int = 10;
        doNullField(foo, "_int");
        assertEquals(0, foo._int);
    }

    @Test
    public void defaultFieldOnLongSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._long = 10;
        doNullField(foo, "_long");
        assertEquals(0, foo._long);
    }

    @Test
    public void defaultFieldOnShortSetsZero() throws NoSuchFieldException {
        AllTypesContainer foo = new AllTypesContainer();
        foo._short = 10;
        doNullField(foo, "_short");
        assertEquals(0, foo._short);
    }

    @SuppressWarnings("static-access")
    @Test
    public void defaultFieldOnPrimitiveStaticFinalIsNoOp() throws NoSuchFieldException {
        StaticFinalContainer foo = new StaticFinalContainer();
        doNullField(foo, "CONST");
        assertEquals(42, foo.CONST);
    }

    @SuppressWarnings("static-access")
    @Test
    public void defaultFieldOnObjectStaticFinalIsNoOp() throws NoSuchFieldException {
        StaticFinalContainer foo = new StaticFinalContainer();
        Object original = foo.OBJECT;
        doNullField(foo, "OBJECT");
        assertSame(original, foo.OBJECT);
    }

    @Test
    public void defaultFieldOnSyntheticIsNoOp() throws NoSuchFieldException {
        Outer outer = new Outer();
        Inner inner = outer.new Inner();
        String fieldName = getSyntheticFieldName(inner, "this$");
        doNullField(inner, fieldName);
        assertSame(outer, inner.getOuter());
    }

    @Test
    public void defaultPrivateField() throws NoSuchFieldException {
        PrivateObjectContainer foo = new PrivateObjectContainer();
        doNullField(foo, FIELD_NAME);
        assertNull(foo.get());
    }

    @Test
    public void copyToPrimitiveField() {
        int value = 10;

        PrimitiveContainer from = new PrimitiveContainer();
        from.field = value;

        PrimitiveContainer to = new PrimitiveContainer();
        doCopyField(to, from, FIELD_NAME);

        assertEquals(value, to.field);
    }

    @Test
    public void copyToObjectField() {
        Object value = new Object();

        ObjectContainer from = new ObjectContainer();
        from.field = value;

        ObjectContainer to = new ObjectContainer();
        doCopyField(to, from, FIELD_NAME);

        assertSame(value, to.field);
    }

    @Test
    public void changeField() {
        AllTypesContainer reference = new AllTypesContainer();
        AllTypesContainer changed = new AllTypesContainer();
        assertTrue(reference.equals(changed));

        for (Field field : FieldIterable.of(AllTypesContainer.class)) {
            new FieldAccessor(changed, field).changeField(prefabValues);
            assertFalse("On field: " + field.getName(), reference.equals(changed));
            new FieldAccessor(reference, field).changeField(prefabValues);
            assertTrue("On field: " + field.getName(), reference.equals(changed));
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void changeFieldOnPrimitiveStaticFinalIsNoOp() throws NoSuchFieldException {
        StaticFinalContainer foo = new StaticFinalContainer();
        doChangeField(foo, "CONST");
        assertEquals(42, foo.CONST);
    }

    @SuppressWarnings("static-access")
    @Test
    public void changeFieldStaticFinal() throws SecurityException, NoSuchFieldException {
        StaticFinalContainer foo = new StaticFinalContainer();
        Object original = foo.OBJECT;
        doChangeField(foo, "OBJECT");
        assertEquals(original, foo.OBJECT);
    }

    @Test
    public void changeAbstractField() {
        AbstractClassContainer foo = new AbstractClassContainer();
        doChangeField(foo, FIELD_NAME);
        assertNotNull(foo.field);
    }

    @Test
    public void changeInterfaceField() {
        InterfaceContainer foo = new InterfaceContainer();
        doChangeField(foo, FIELD_NAME);
        assertNotNull(foo.field);
    }

    @Test
    public void changeArrayField() {
        AllArrayTypesContainer reference = new AllArrayTypesContainer();
        AllArrayTypesContainer changed = new AllArrayTypesContainer();
        assertTrue(reference.equals(changed));

        for (Field field : FieldIterable.of(AllArrayTypesContainer.class)) {
            new FieldAccessor(changed, field).changeField(prefabValues);
            assertFalse("On field: " + field.getName(), reference.equals(changed));
            new FieldAccessor(reference, field).changeField(prefabValues);
            assertTrue("On field: " + field.getName(), reference.equals(changed));
        }
    }

    @Test
    public void changeAbstractArrayField() {
        AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
        doChangeField(foo, "abstractClasses");
        assertNotNull(foo.abstractClasses[0]);
    }

    @Test
    public void changeInterfaceArrayField() {
        AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
        doChangeField(foo, "interfaces");
        assertNotNull(foo.interfaces[0]);
    }

    @Test
    public void addPrefabValues() {
        PointContainer foo = new PointContainer(new Point(1, 2));
        prefabValues.put(Point.class, RED_NEW_POINT, BLACK_NEW_POINT);

        doChangeField(foo, "point");
        assertEquals(RED_NEW_POINT, foo.getPoint());

        doChangeField(foo, "point");
        assertEquals(BLACK_NEW_POINT, foo.getPoint());

        doChangeField(foo, "point");
        assertEquals(RED_NEW_POINT, foo.getPoint());
    }

    @Test
    public void addPrefabArrayValues() {
        PointArrayContainer foo = new PointArrayContainer();
        prefabValues.put(Point.class, RED_NEW_POINT, BLACK_NEW_POINT);

        doChangeField(foo, "points");
        assertEquals(RED_NEW_POINT, foo.points[0]);

        doChangeField(foo, "points");
        assertEquals(BLACK_NEW_POINT, foo.points[0]);

        doChangeField(foo, "points");
        assertEquals(RED_NEW_POINT, foo.points[0]);
    }

    private Object getValue(Object object, String fieldName) {
        return getAccessorFor(object, fieldName).get();
    }

    private void setField(Object object, String fieldName, Object value) {
        getAccessorFor(object, fieldName).set(value);
    }

    private void doNullField(Object object, String fieldName) {
        getAccessorFor(object, fieldName).defaultField();
    }

    private void doCopyField(Object to, Object from, String fieldName) {
        getAccessorFor(from, fieldName).copyTo(to);
    }

    private void doChangeField(Object object, String fieldName) {
        getAccessorFor(object, fieldName).changeField(prefabValues);
    }

    private FieldAccessor getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return new FieldAccessor(object, field);
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }

    private String getSyntheticFieldName(Object object, String prefix) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getName().startsWith(prefix)) {
                return field.getName();
            }
        }
        throw new IllegalStateException("Cannot find internal field starting with " + prefix);
    }
}
