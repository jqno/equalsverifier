package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.*;

import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class ValidationsTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Validations.class);
    }

    @Test
    void validateFieldTypeMatches_shouldFailOnWrongType() {
        assertThat(true)
                .satisfies(
                    b -> ExpectedException
                            .when(
                                () -> Validations
                                        .validateFieldTypeMatches(TestContainer.class, "listField", HashSet.class))
                            .assertThrows(IllegalStateException.class)
                            .assertMessageContains("should be of type List", "but are HashSet"),
                    b -> ExpectedException
                            .when(
                                () -> Validations
                                        .validateFieldTypeMatches(TestContainer.class, "objectField", int.class))
                            .assertThrows(IllegalStateException.class)
                            .assertMessageContains("should be of type Object", "but are int"),
                    b -> ExpectedException
                            .when(
                                () -> Validations
                                        .validateFieldTypeMatches(TestContainer.class, "charsField", Character.class))
                            .assertThrows(IllegalStateException.class)
                            .assertMessageContains("should be of type CharSequence", "but are Character"));
    }

    @Test
    void validateFieldTypeMatches_shouldAllowSubTypes() {
        assertThat(true)
                .satisfies(
                    b -> assertThatCode(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", ArrayList.class))
                            .as("Should allow ArrayList as a List")
                            .doesNotThrowAnyException(),
                    b -> assertThatCode(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "listField", ArrayList.class))
                            .as("Should allow ArrayList as a List")
                            .doesNotThrowAnyException(),
                    b -> assertThatCode(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "objectField", Integer.class))
                            .as("Should allow Integer as an Object")
                            .doesNotThrowAnyException(),
                    b -> assertThatCode(
                        () -> Validations.validateFieldTypeMatches(TestContainer.class, "charsField", String.class))
                            .as("Should allow String as a CharSequence")
                            .doesNotThrowAnyException());
    }

    @Test
    void validateFieldTypeMatches_shouldFailOnSuperTypes() {
        assertThat(true)
                .satisfies(
                    b -> ExpectedException
                            .when(
                                () -> Validations
                                        .validateFieldTypeMatches(TestContainer.class, "listField", Collection.class))
                            .assertThrows(IllegalStateException.class)
                            .assertMessageContains("should be of type List", "but are Collection"),
                    b -> ExpectedException
                            .when(
                                () -> Validations
                                        .validateFieldTypeMatches(TestContainer.class, "charsField", Object.class))
                            .assertThrows(IllegalStateException.class)
                            .assertMessageContains("should be of type CharSequence", "but are Object"));
    }

    @SuppressWarnings("unused")
    private static final class TestContainer {

        List<String> listField;
        Object objectField;
        CharSequence charsField;
    }
}
