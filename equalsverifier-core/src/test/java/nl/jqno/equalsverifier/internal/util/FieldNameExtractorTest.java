package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class FieldNameExtractorTest {

    private static final String FIELD_NOT_FOUND = "field not found: ";

    private static final String FIELD_STRING = "fieldString";
    private static final String FIELD_OBJECT = "fieldObject";
    private static final String FIELD_LIST = "fieldList";
    private static final String FIELD_PRIMITIVE_INT = "fieldPrimitiveInt";

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(FieldNameExtractor.class);
    }

    @Test
    public void should_extractFields_succesfully() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(
            FieldNameExtractorTestHelper.class
        );

        assertTrue(fields.contains(FIELD_STRING), FIELD_NOT_FOUND + FIELD_STRING);
        assertTrue(fields.contains(FIELD_OBJECT), FIELD_NOT_FOUND + FIELD_OBJECT);
        assertTrue(fields.contains(FIELD_LIST), FIELD_NOT_FOUND + FIELD_LIST);
        assertTrue(fields.contains(FIELD_PRIMITIVE_INT), FIELD_NOT_FOUND + FIELD_PRIMITIVE_INT);

        assertTrue(4 == fields.size(), "Total number of fields was not equal to expected value");
    }

    @Test
    public void should_disallow_adding_extra_fields() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(
            FieldNameExtractorTestHelper.class
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> fields.add("illegally added field")
        );
    }

    @Test
    public void should_disallow_removing_fields() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(
            FieldNameExtractorTestHelper.class
        );

        assertThrows(UnsupportedOperationException.class, () -> fields.remove(FIELD_STRING));
    }

    class FieldNameExtractorTestHelper {

        public final String fieldString;
        protected int fieldPrimitiveInt;
        final List<Integer> fieldList;
        private final Object fieldObject;

        public FieldNameExtractorTestHelper(
            String fieldString,
            List<Integer> fieldList,
            Object fieldObject
        ) {
            this.fieldString = fieldString;
            this.fieldList = fieldList;
            this.fieldObject = fieldObject;
        }

        public void setFieldPrimitiveInt(int newInt) {
            this.fieldPrimitiveInt = newInt;
        }
    }
}
