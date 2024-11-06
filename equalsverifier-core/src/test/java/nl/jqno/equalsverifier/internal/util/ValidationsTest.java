package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.util.*;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class ValidationsTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Validations.class);
    }

    @Test
    public void validateFieldTypeMatches_shouldFailOnWrongType() {
        assertAll(
            () -> {
                ExpectedException
                    .when(() ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "listField"),
                            HashSet.class
                        )
                    )
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type List", "but are HashSet");
            },
            () -> {
                ExpectedException
                    .when(() ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "objectField"),
                            int.class
                        )
                    )
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type Object", "but are int");
            },
            () -> {
                ExpectedException
                    .when(() ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "charsField"),
                            Character.class
                        )
                    )
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type CharSequence", "but are Character");
            }
        );
    }

    @Test
    public void validateFieldTypeMatches_shouldAllowSubTypes() {
        assertAll(
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "listField"),
                            ArrayList.class
                        ),
                    "Should allow ArrayList as a List"
                ),
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "objectField"),
                            Integer.class
                        ),
                    "Should allow Integer as an Object"
                ),
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "charsField"),
                            String.class
                        ),
                    "Should allow String as a CharSequence"
                )
        );
    }

    @Test
    public void validateFieldTypeMatches_shouldFailOnSuperTypes() {
        assertAll(
            () -> {
                ExpectedException
                    .when(() ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "listField"),
                            Collection.class
                        )
                    )
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type List", "but are Collection");
            },
            () -> {
                ExpectedException
                    .when(() ->
                        Validations.validateFieldTypeMatches(
                            getField(TestContainer.class, "charsField"),
                            Object.class
                        )
                    )
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type CharSequence", "but are Object");
            }
        );
    }

    private Field getField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Class " + type + " has no field " + fieldName);
        }
    }

    @SuppressWarnings("unused")
    private static final class TestContainer {

        List<String> listField;
        Object objectField;
        CharSequence charsField;
    }
}
