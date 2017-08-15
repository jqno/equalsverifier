package nl.jqno.equalsverifier.internal.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FieldNameExtractorTest {
    private static final String FIELD_NOT_FOUND = "field not found: ";

    private static final String FIELD_STRING = "fieldString";
    private static final String FIELD_OBJECT = "fieldObject";
    private static final String FIELD_LIST = "fieldList";
    private static final String FIELD_PRIMITIVE_INT = "fieldPrimitiveInt";

    @Test
    public void should_extractFields_succesfully() throws Exception {
        Set<String> fields = FieldNameExtractor.extractFields(FieldNameExtractorTestHelper.class);

        assertTrue(FIELD_NOT_FOUND + FIELD_STRING, fields.contains(FIELD_STRING));
        assertTrue(FIELD_NOT_FOUND + FIELD_OBJECT, fields.contains(FIELD_OBJECT));
        assertTrue(FIELD_NOT_FOUND + FIELD_LIST, fields.contains(FIELD_LIST));
        assertTrue(FIELD_NOT_FOUND + FIELD_PRIMITIVE_INT, fields.contains(FIELD_PRIMITIVE_INT));

        assertTrue("Total number of fields was not equal to expected value", 4 == fields.size());
    }

}


class FieldNameExtractorTestHelper {
    public final String fieldString;
    private final Object fieldObject;
    final List<Integer> fieldList;
    protected int fieldPrimitiveInt;

    public FieldNameExtractorTestHelper(String fieldString, Object fieldObject, List<Integer> fieldList) {
        this.fieldString = fieldString;
        this.fieldObject = fieldObject;
        this.fieldList = fieldList;
    }

    public int getFieldPrimitiveInt() {
        return fieldPrimitiveInt;
    }

    public void setFieldPrimitiveInt(int fieldPrimitiveInt) {
        this.fieldPrimitiveInt = fieldPrimitiveInt;
    }
}