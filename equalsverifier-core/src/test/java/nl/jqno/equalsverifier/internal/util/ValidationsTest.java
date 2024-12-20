package nl.jqno.equalsverifier.internal.util;

// CHECKSTYLE OFF: IllegalImport

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class ValidationsTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Validations.class);
    }

    @Test
    void validateFieldTypeMatches_shouldFailOnWrongType() {
        assertAll(() -> {
            ExpectedException
                    .when(() -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", HashSet.class))
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type List", "but are HashSet");
        }, () -> {
            ExpectedException
                    .when(() -> Validations.validateFieldTypeMatches(TestContainer.class, "objectField", int.class))
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type Object", "but are int");
        }, () -> {
            ExpectedException
                    .when(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "charsField", Character.class))
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type CharSequence", "but are Character");
        });
    }

    @Test
    void validateFieldTypeMatches_shouldAllowSubTypes() {
        assertAll(
            () -> assertThatCode(
                () -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", ArrayList.class))
                    .as("Should allow ArrayList as a List")
                    .doesNotThrowAnyException(),
            () -> assertThatCode(
                () -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", ArrayList.class))
                    .as("Should allow ArrayList as a List")
                    .doesNotThrowAnyException(),
            () -> assertThatCode(
                () -> Validations.validateFieldTypeMatches(TestContainer.class, "objectField", Integer.class))
                    .as("Should allow Integer as an Object")
                    .doesNotThrowAnyException(),
            () -> assertThatCode(
                () -> Validations.validateFieldTypeMatches(TestContainer.class, "charsField", String.class))
                    .as("Should allow String as a CharSequence")
                    .doesNotThrowAnyException());
    }

    @Test
    void validateFieldTypeMatches_shouldFailOnSuperTypes() {
        assertAll(() -> {
            ExpectedException
                    .when(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", Collection.class))
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type List", "but are Collection");
        }, () -> {
            ExpectedException
                    .when(() -> Validations.validateFieldTypeMatches(TestContainer.class, "charsField", Object.class))
                    .assertThrows(IllegalStateException.class)
                    .assertMessageContains("should be of type CharSequence", "but are Object");
        });
    }

    @SuppressWarnings("unused")
    private static final class TestContainer {

        List<String> listField;
        Object objectField;
        CharSequence charsField;
    }
}
