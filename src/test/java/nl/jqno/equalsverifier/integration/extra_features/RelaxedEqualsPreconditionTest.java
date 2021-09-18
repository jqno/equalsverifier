package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Multiple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RelaxedEqualsPreconditionTest {

    private static final String PRECONDITION = "Precondition";
    private static final String DIFFERENT_CLASSES = "are of different classes";
    private static final String TWO_IDENTICAL_OBJECTS_APPEAR = "two identical objects appear";
    private static final String NOT_ALL_EQUAL_OBJECT_ARE_EQUAL = "not all equal objects are equal";
    private static final String EQUAL_IS_UNEQUAL =
        "an equal example also appears as unequal example.";
    private static final String OBJECT_APPEARS_TWICE = "the same object appears twice";
    private static final String TWO_OBJECTS_ARE_EQUAL = "two objects are equal to each other";

    private Multiple red;
    private Multiple blue;
    private Multiple green;

    @BeforeEach
    public void setup() {
        red = new Multiple(1, 2);
        blue = new Multiple(2, 1);
        green = new Multiple(2, 2);
    }

    @Test
    public void throw_whenTheFirstExampleIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(null, blue))
            .assertThrows(IllegalArgumentException.class)
            .assertMessageContains("First example is null.");
    }

    @Test
    public void throw_whenTheSecondExampleIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(red, null))
            .assertThrows(IllegalArgumentException.class)
            .assertMessageContains("Second example is null.");
    }

    @Test
    public void succeed_whenTheVarargArrayIsNull() {
        EqualsVerifier
            .forRelaxedEqualExamples(red, blue, (Multiple[]) null)
            .andUnequalExample(green)
            .verify();
    }

    @Test
    public void fail_whenAVarargParameterIsNull() {
        Multiple another = new Multiple(-1, -2);
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(red, blue, another, null))
            .assertThrows(IllegalArgumentException.class)
            .assertMessageContains("One of the examples is null.");
    }

    @Test
    public void fail_whenTheUnequalExampleIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(red, blue).andUnequalExample(null))
            .assertThrows(IllegalArgumentException.class)
            .assertMessageContains("First example is null.");
    }

    @Test
    public void succeed_whenTheUnequalVarargArrayIsNull() {
        EqualsVerifier
            .forRelaxedEqualExamples(red, blue)
            .andUnequalExamples(green, (Multiple[]) null)
            .verify();
    }

    @Test
    public void fail_whenAnUnequalVarargParameterIsNull() {
        Multiple another = new Multiple(3, 3);
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forRelaxedEqualExamples(red, blue)
                    .andUnequalExamples(green, another, null)
            )
            .assertThrows(IllegalArgumentException.class);
    }

    @Test
    public void fail_whenEqualExamplesAreOfDifferentRuntimeTypes() {
        SubMultiple sm = new SubMultiple(1, 2);
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(sm, red).andUnequalExample(green).verify()
            )
            .assertFailure()
            .assertMessageContains(
                PRECONDITION,
                DIFFERENT_CLASSES,
                SubMultiple.class.getSimpleName(),
                Multiple.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenTheSameExampleIsGivenTwice() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(red, red).andUnequalExample(green).verify()
            )
            .assertFailure()
            .assertMessageContains(
                PRECONDITION,
                OBJECT_APPEARS_TWICE,
                Multiple.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenTwoExamplesAreIdentical() {
        Multiple aa = new Multiple(1, 2);
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(red, aa).andUnequalExample(green).verify()
            )
            .assertFailure()
            .assertMessageContains(
                PRECONDITION,
                TWO_IDENTICAL_OBJECTS_APPEAR,
                Multiple.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenTwoExamplesAreNotEqualAtAll() {
        Multiple aa = new Multiple(42, 42);
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(red, aa).andUnequalExample(green).verify()
            )
            .assertFailure()
            .assertMessageContains(
                PRECONDITION,
                NOT_ALL_EQUAL_OBJECT_ARE_EQUAL,
                Multiple.class.getSimpleName()
            );
    }

    @Test
    public void fail_whenAnEqualExampleIsAlsoGivenAsAnUnequalExample() {
        ExpectedException
            .when(() -> EqualsVerifier.forRelaxedEqualExamples(red, green).andUnequalExample(green))
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(PRECONDITION, EQUAL_IS_UNEQUAL);
    }

    @Test
    public void fail_whenTheSameUnequalExampleIsGivenTwice() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(red, blue).andUnequalExamples(green, green)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(PRECONDITION, TWO_OBJECTS_ARE_EQUAL);
    }

    @Test
    public void fail_whenTwoUnequalExamplesAreEqualToEachOther() {
        Multiple xx = new Multiple(2, 2);
        ExpectedException
            .when(() ->
                EqualsVerifier.forRelaxedEqualExamples(red, blue).andUnequalExamples(green, xx)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(PRECONDITION, TWO_OBJECTS_ARE_EQUAL);
    }

    public static class SubMultiple extends Multiple {

        public SubMultiple(int a, int b) {
            super(a, b);
        }
    }
}
