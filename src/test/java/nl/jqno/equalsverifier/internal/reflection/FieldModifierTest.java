package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Outer.Inner;
import org.junit.Before;
import org.junit.Test;

public class FieldModifierTest {
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
    public void defaultFieldOnObjectSetsNull() {
        ObjectContainer foo = new ObjectContainer();
        foo.field = new Object();
        doNullField(foo, FIELD_NAME);
        assertNull(foo.field);
    }

    @Test
    public void defaultFieldOnArraySetsNull() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._array = new int[] {1, 2, 3};
        doNullField(foo, "_array");
        assertNull(foo._array);
    }

    @Test
    public void defaultFieldOnBooleanSetsFalse() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._boolean = true;
        doNullField(foo, "_boolean");
        assertEquals(false, foo._boolean);
    }

    @Test
    public void defaultFieldOnByteSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._byte = 10;
        doNullField(foo, "_byte");
        assertEquals(0, foo._byte);
    }

    @Test
    public void defaultFieldOnCharSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._char = 'a';
        doNullField(foo, "_char");
        assertEquals('\u0000', foo._char);
    }

    @Test
    public void defaultFieldOnDoubleSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._double = 1.1;
        doNullField(foo, "_double");
        assertEquals(0.0, foo._double, 0.0000001);
    }

    @Test
    public void defaultFieldOnFloatSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._float = 1.1f;
        doNullField(foo, "_float");
        assertEquals(0.0f, foo._float, 0.0000001);
    }

    @Test
    public void defaultFieldOnIntSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._int = 10;
        doNullField(foo, "_int");
        assertEquals(0, foo._int);
    }

    @Test
    public void defaultFieldOnLongSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._long = 10;
        doNullField(foo, "_long");
        assertEquals(0, foo._long);
    }

    @Test
    public void defaultFieldOnShortSetsZero() {
        AllTypesContainer foo = new AllTypesContainer();
        foo._short = 10;
        doNullField(foo, "_short");
        assertEquals(0, foo._short);
    }

    @SuppressWarnings("static-access")
    @Test
    public void defaultFieldOnPrimitiveStaticFinalIsNoOp() {
        StaticFinalContainer foo = new StaticFinalContainer();
        doNullField(foo, "CONST");
        assertEquals(42, foo.CONST);
    }

    @SuppressWarnings("static-access")
    @Test
    public void defaultFieldOnObjectStaticFinalIsNoOp() {
        StaticFinalContainer foo = new StaticFinalContainer();
        Object original = foo.OBJECT;
        doNullField(foo, "OBJECT");
        assertSame(original, foo.OBJECT);
    }

    @Test
    public void defaultFieldOnSyntheticIsNoOp() {
        Outer outer = new Outer();
        Inner inner = outer.new Inner();
        String fieldName = getSyntheticFieldName(inner, "this$");
        doNullField(inner, fieldName);
        assertSame(outer, inner.getOuter());
    }

    @Test
    public void defaultPrivateField() {
        PrivateObjectContainer foo = new PrivateObjectContainer();
        doNullField(foo, FIELD_NAME);
        assertNull(foo.get());
    }

    @Test
    public void defaultStaticField() {
        StaticContainer foo = new StaticContainer();
        getAccessorFor(foo, "field").defaultStaticField();
        assertNull(StaticContainer.field);
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
            new FieldModifier(changed, field).changeField(prefabValues, TypeTag.NULL);
            assertFalse("On field: " + field.getName(), reference.equals(changed));
            new FieldModifier(reference, field).changeField(prefabValues, TypeTag.NULL);
            assertTrue("On field: " + field.getName(), reference.equals(changed));
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void changeFieldOnPrimitiveStaticFinalIsNoOp() {
        StaticFinalContainer foo = new StaticFinalContainer();
        doChangeField(foo, "CONST");
        assertEquals(42, foo.CONST);
    }

    @SuppressWarnings("static-access")
    @Test
    public void changeFieldStaticFinal() throws SecurityException {
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
            new FieldModifier(changed, field).changeField(prefabValues, TypeTag.NULL);
            assertFalse("On field: " + field.getName(), reference.equals(changed));
            new FieldModifier(reference, field).changeField(prefabValues, TypeTag.NULL);
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
    public void changeGenericField() {
        GenericListContainer foo = new GenericListContainer();
        doChangeField(foo, "stringList");
        doChangeField(foo, "integerList");
        assertNotEquals(foo.stringList, foo.integerList);
    }

    @Test
    public void changeTypeVariableGenericField() {
        GenericTypeVariableListContainer<String> foo = new GenericTypeVariableListContainer<>();
        doChangeField(
                foo,
                "tList",
                new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)));
        assertFalse(foo.tList.isEmpty());
    }

    @Test
    public void addPrefabValues() {
        PointContainer foo = new PointContainer(new Point(1, 2));

        doChangeField(foo, "point");
        assertEquals(RED_NEW_POINT, foo.getPoint());

        doChangeField(foo, "point");
        assertEquals(BLUE_NEW_POINT, foo.getPoint());

        doChangeField(foo, "point");
        assertEquals(RED_NEW_POINT, foo.getPoint());
    }

    @Test
    public void addPrefabArrayValues() {
        PointArrayContainer foo = new PointArrayContainer();

        doChangeField(foo, "points");
        assertEquals(RED_NEW_POINT, foo.points[0]);

        doChangeField(foo, "points");
        assertEquals(BLUE_NEW_POINT, foo.points[0]);

        doChangeField(foo, "points");
        assertEquals(RED_NEW_POINT, foo.points[0]);
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
        doChangeField(object, fieldName, TypeTag.NULL);
    }

    private void doChangeField(Object object, String fieldName, TypeTag enclosingType) {
        getAccessorFor(object, fieldName).changeField(prefabValues, enclosingType);
    }

    private FieldModifier getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return new FieldModifier(object, field);
        } catch (NoSuchFieldException e) {
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
