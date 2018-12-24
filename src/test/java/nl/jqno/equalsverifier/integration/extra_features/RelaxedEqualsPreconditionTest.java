package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Multiple;
import org.junit.Before;
import org.junit.Test;

public class RelaxedEqualsPreconditionTest extends ExpectedExceptionTestBase {
    private static final String PRECONDITION = "Precondition";
    private static final String DIFFERENT_CLASSES = "are of different classes";
    private static final String TWO_IDENTICAL_OBJECTS_APPEAR = "two identical objects appear";
    private static final String NOT_ALL_EQUAL_OBJECT_ARE_EQUAL = "not all equal objects are equal";
    private static final String EQUAL_IS_UNEQUAL = "an equal example also appears as unequal example.";
    private static final String OBJECT_APPEARS_TWICE = "the same object appears twice";
    private static final String TWO_OBJECTS_ARE_EQUAL = "two objects are equal to each other";

    private Multiple red;
    private Multiple black;
    private Multiple green;

    @Before
    public void setup() {
        red = new Multiple(1, 2);
        black = new Multiple(2, 1);
        green = new Multiple(2, 2);
    }

    @Test
    public void throw_whenTheFirstExampleIsNull() {
        expectException(IllegalArgumentException.class, "First example is null.");
        EqualsVerifier.forRelaxedEqualExamples(null, black);
    }

    @Test
    public void throw_whenTheSecondExampleIsNull() {
        expectException(IllegalArgumentException.class, "Second example is null.");
        EqualsVerifier.forRelaxedEqualExamples(red, null);
    }

    @Test
    public void succeed_whenTheVarargArrayIsNull() {
        EqualsVerifier.forRelaxedEqualExamples(red, black, (Multiple[])null)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenAVarargParameterIsNull() {
        expectException(IllegalArgumentException.class, "One of the examples is null.");
        Multiple another = new Multiple(-1, -2);
        EqualsVerifier.forRelaxedEqualExamples(red, black, another, null);
    }

    @Test
    public void fail_whenTheUnequalExampleIsNull() {
        expectException(IllegalArgumentException.class, "First example is null.");
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExample(null);
    }

    @Test
    public void succeed_whenTheUnequalVarargArrayIsNull() {
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, (Multiple[])null)
                .verify();
    }

    @Test
    public void fail_whenAnUnequalVarargParameterIsNull() {
        expectException(IllegalArgumentException.class);
        Multiple another = new Multiple(3, 3);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, another, null);
    }

    @Test
    public void fail_whenEqualExamplesAreOfDifferentRuntimeTypes() {
        expectFailure(PRECONDITION, DIFFERENT_CLASSES, SubMultiple.class.getSimpleName(), Multiple.class.getSimpleName());
        SubMultiple sm = new SubMultiple(1, 2);
        EqualsVerifier.forRelaxedEqualExamples(sm, red)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenTheSameExampleIsGivenTwice() {
        expectFailure(PRECONDITION, OBJECT_APPEARS_TWICE, Multiple.class.getSimpleName());
        EqualsVerifier.forRelaxedEqualExamples(red, red)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenTwoExamplesAreIdentical() {
        expectFailure(PRECONDITION, TWO_IDENTICAL_OBJECTS_APPEAR, Multiple.class.getSimpleName());
        Multiple aa = new Multiple(1, 2);
        EqualsVerifier.forRelaxedEqualExamples(red, aa)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenTwoExamplesAreNotEqualAtAll() {
        expectFailure(PRECONDITION, NOT_ALL_EQUAL_OBJECT_ARE_EQUAL, Multiple.class.getSimpleName());
        Multiple aa = new Multiple(42, 42);
        EqualsVerifier.forRelaxedEqualExamples(red, aa)
                .andUnequalExample(green)
                .verify();
    }

    @Test
    public void fail_whenAnEqualExampleIsAlsoGivenAsAnUnequalExample() {
        expectException(IllegalStateException.class, PRECONDITION, EQUAL_IS_UNEQUAL);
        EqualsVerifier.forRelaxedEqualExamples(red, green)
                .andUnequalExample(green);
    }

    @Test
    public void fail_whenTheSameUnequalExampleIsGivenTwice() {
        expectException(IllegalStateException.class, PRECONDITION, TWO_OBJECTS_ARE_EQUAL);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, green);
    }

    @Test
    public void fail_whenTwoUnequalExamplesAreEqualToEachOther() {
        expectException(IllegalStateException.class, PRECONDITION, TWO_OBJECTS_ARE_EQUAL);
        Multiple xx = new Multiple(2, 2);
        EqualsVerifier.forRelaxedEqualExamples(red, black)
                .andUnequalExamples(green, xx);
    }

    public static class SubMultiple extends Multiple {
        public SubMultiple(int a, int b) {
            super(a, b);
        }
    }
}
