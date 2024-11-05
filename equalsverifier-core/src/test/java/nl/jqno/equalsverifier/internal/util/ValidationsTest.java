package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

/** Tests for {@link Validations} */
class ValidationsTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Validations.class);
    }

    /**
     * {@link Validations#validateFieldTypeMatches(Class, String, Class)} should
     * throw if the field type does not match the given type.
     */
    @Test
    void validateFieldTypeMatches_ShouldFailOnWrongType() {
        assertAll(
            () -> {
                Exception exc = assertThrows(
                    IllegalStateException.class,
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "listField",
                            HashSet.class
                        ),
                    "Should not allow HashSet as a List"
                );
                assertThat(
                    exc.getMessage(),
                    allOf(
                        containsString("should be of type List"),
                        containsString("but are HashSet")
                    )
                );
            },
            () -> {
                Exception exc = assertThrows(
                    IllegalStateException.class,
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "objectField",
                            int.class
                        ),
                    "Should not allow primitives as an Object, because primitives are not a subtype of Object"
                );
                assertThat(
                    exc.getMessage(),
                    allOf(containsString("should be of type Object"), containsString("but are int"))
                );
            },
            () -> {
                Exception exc = assertThrows(
                    IllegalStateException.class,
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "charsField",
                            Character.class
                        ),
                    "Should not allow Character as a CharSequence"
                );
                assertThat(
                    exc.getMessage(),
                    allOf(
                        containsString("should be of type CharSequence"),
                        containsString("but are Character")
                    )
                );
            }
        );
    }

    /**
     * {@link Validations#validateFieldTypeMatches(Class, String, Class)} should not
     * throw if the field type is a super-type or interface of the given type.
     */
    @Test
    void validateFieldTypeMatches_ShouldAllowSubTypes() {
        assertAll(
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "listField",
                            ArrayList.class
                        ),
                    "Should allow ArrayList as a List"
                ),
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "objectField",
                            Integer.class
                        ),
                    "Should allow Integer as an Object"
                ),
            () ->
                assertDoesNotThrow(
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "charsField",
                            String.class
                        ),
                    "Should allow String as a CharSequence"
                )
        );
    }

    /**
     * {@link Validations#validateFieldTypeMatches(Class, String, Class)} should not
     * throw if the field type is a sub-type of the given type or interface.
     */
    @Test
    void validateFieldTypeMatches_ShouldFailOnSuperTypes() {
        assertAll(
            () -> {
                Exception exc = assertThrows(
                    IllegalStateException.class,
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "listField",
                            Collection.class
                        ),
                    "Should not allow Collection as a List"
                );
                assertThat(
                    exc.getMessage(),
                    allOf(
                        containsString("should be of type List"),
                        containsString("but are Collection")
                    )
                );
            },
            () -> {
                Exception exc = assertThrows(
                    IllegalStateException.class,
                    () ->
                        Validations.validateFieldTypeMatches(
                            TestContainer.class,
                            "charsField",
                            Object.class
                        ),
                    "Should not allow Object as a CharSequence"
                );
                assertThat(
                    exc.getMessage(),
                    allOf(
                        containsString("should be of type CharSequence"),
                        containsString("but are Object")
                    )
                );
            }
        );
    }

    /** Test class for type checking. */
    private static class TestContainer {

        List<String> listField;
        Object objectField;
        CharSequence charsField;

        @Override
        public int hashCode() {
            return Objects.hash(charsField, listField, objectField);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TestContainer)) {
                return false;
            }
            TestContainer other = (TestContainer) obj;
            return (
                Objects.equals(charsField, other.charsField) &&
                Objects.equals(listField, other.listField) &&
                Objects.equals(objectField, other.objectField)
            );
        }
    }
}
