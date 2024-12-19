package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class FieldNameExtractorTest {

    private static final String FIELD_NOT_FOUND = "field not found: ";

    private static final String FIELD_STRING = "fieldString";
    private static final String FIELD_OBJECT = "fieldObject";
    private static final String FIELD_LIST = "fieldList";
    private static final String FIELD_PRIMITIVE_INT = "fieldPrimitiveInt";

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(FieldNameExtractor.class);
    }

    @Test
    void should_extractFields_succesfully() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        assertThat(fields.contains(FIELD_STRING)).as(FIELD_NOT_FOUND + FIELD_STRING).isTrue();
        assertThat(fields.contains(FIELD_OBJECT)).as(FIELD_NOT_FOUND + FIELD_OBJECT).isTrue();
        assertThat(fields.contains(FIELD_LIST)).as(FIELD_NOT_FOUND + FIELD_LIST).isTrue();
        assertThat(fields.contains(FIELD_PRIMITIVE_INT)).as(FIELD_NOT_FOUND + FIELD_PRIMITIVE_INT).isTrue();

        assertThat(fields.size()).as("Total number of fields was not equal to expected value").isEqualTo(4);
    }

    @Test
    void should_disallow_adding_extra_fields() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> fields.add("illegally added field"));
    }

    @Test
    void should_disallow_removing_fields() {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> fields.remove(FIELD_STRING));
    }

    class FieldNameExtractorTestHelper {

        public final String fieldString;
        protected int fieldPrimitiveInt;
        final List<Integer> fieldList;
        private final Object fieldObject;

        public FieldNameExtractorTestHelper(String fieldString, List<Integer> fieldList, Object fieldObject) {
            this.fieldString = fieldString;
            this.fieldList = fieldList;
            this.fieldObject = fieldObject;
        }

        public void setFieldPrimitiveInt(int newInt) {
            this.fieldPrimitiveInt = newInt;
        }
    }
}
