package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.Before;
import org.junit.Test;

public class FieldAccessorTest {
    private static final Point RED_NEW_POINT = new Point(10, 20);
    private static final Point BLUE_NEW_POINT = new Point(20, 10);
    private static final Point REDCOPY_NEW_POINT = new Point(10, 20);
    private static final String FIELD_NAME = "field";

    private PrefabValues prefabValues;

    @Before
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        factoryCache.put(Point.class, values(RED_NEW_POINT, BLUE_NEW_POINT, REDCOPY_NEW_POINT));
        prefabValues = new PrefabValues(factoryCache);
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
        assertFalse(fieldAccessor.fieldIsEmptyOrSingleValueEnum());
    }

    @Test
    public void isEnumButNotSingleValue() {
        EnumContainer foo = new EnumContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, "twoElementEnum");
        assertFalse(fieldAccessor.fieldIsEmptyOrSingleValueEnum());
    }

    @Test
    public void isSingleValueEnum() {
        EnumContainer foo = new EnumContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, "oneElementEnum");
        assertTrue(fieldAccessor.fieldIsEmptyOrSingleValueEnum());
    }

    @Test
    public void isEmptyEnum() {
        EnumContainer foo = new EnumContainer();
        FieldAccessor fieldAccessor = getAccessorFor(foo, "emptyEnum");
        assertTrue(fieldAccessor.fieldIsEmptyOrSingleValueEnum());
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

    private Object getValue(Object object, String fieldName) {
        return getAccessorFor(object, fieldName).get();
    }

    private FieldAccessor getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return new FieldAccessor(object, field);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }
}
