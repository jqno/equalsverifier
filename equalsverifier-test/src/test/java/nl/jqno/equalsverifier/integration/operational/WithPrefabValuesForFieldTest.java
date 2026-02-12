package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.PreconditionTypeHelper.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
class WithPrefabValuesForFieldTest {

    private final FinalPoint pRed = new FinalPoint(3, 42);
    private final FinalPoint pBlue = new FinalPoint(3, 1337);
    private final FinalPoint pRedCopy = new FinalPoint(3, 42);
    private final int iRed = 111;
    private final int iBlue = 142;

    @Test
    void fail_whenClassHasSinglePrecondition() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SinglePrecondition.class).suppress(Warning.NULL_FIELDS).verify())
                .assertFailure()
                .assertMessageContains("x coordinate must be");
    }

    @Test
    void fail_whenClassHasSinglePrecondition_record() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePreconditionRecord.class)
                            .suppress(Warning.NULL_FIELDS)
                            .verify())
                .assertFailure()
                .assertMessageContains("Record: failed to run constructor for record")
                .assertCauseMessageContains("i must be between");
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenPrefabValuesForField() {
        EqualsVerifier.forClass(SinglePrecondition.class).withPrefabValuesForField("point", pRed, pBlue).verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenPrefabValuesForField_redCopy() {
        EqualsVerifier
                .forClass(SinglePrecondition.class)
                .withPrefabValuesForField("point", pRed, pBlue, pRedCopy)
                .verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenPrefabValuesForField_record() {
        EqualsVerifier.forClass(SinglePreconditionRecord.class).withPrefabValuesForField("i", iRed, iBlue).verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenPrefabValuesForField_record_redCopy() {
        EqualsVerifier
                .forClass(SinglePreconditionRecord.class)
                .withPrefabValuesForField("i", iRed, iBlue, iRed)
                .verify();
    }

    @Test
    void fail_whenClassHasDualPrecondition() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(DualPrecondition.class).verify())
                .assertFailure()
                .assertMessageContains("x must be between");
    }

    @Test
    void fail_whenClassHasDualPrecondition_record() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(DualPreconditionRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record: failed to run constructor for record")
                .assertCauseMessageContains("x must be between");
    }

    @Test
    void fail_whenClassHasDualPrecondition_givenPrefabValuesForOnlyOneField() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(DualPrecondition.class)
                            .withPrefabValuesForField("x", iRed, iBlue)
                            .verify())
                .assertFailure()
                .assertMessageContains("y must be between");
    }

    @Test
    void fail_whenClassHasDualPrecondition_givenPrefabValuesForOnlyOneField_redCopy() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(DualPrecondition.class)
                            .withPrefabValuesForField("x", iRed, iBlue, iRed)
                            .verify())
                .assertFailure()
                .assertMessageContains("y must be between");
    }

    @Test
    void fail_whenClassHasDualPrecondition_givenPrefabValuesForOnlyOneField_record() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(DualPreconditionRecord.class)
                            .withPrefabValuesForField("x", iRed, iBlue)
                            .verify())
                .assertFailure()
                .assertMessageContains("Record: failed to run constructor for record")
                .assertCauseMessageContains("y must be between");
    }

    @Test
    void fail_whenClassHasDualPrecondition_givenPrefabValuesForOnlyOneField_record_redCopy() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(DualPreconditionRecord.class)
                            .withPrefabValuesForField("x", iRed, iBlue, iRed)
                            .verify())
                .assertFailure()
                .assertMessageContains("Record: failed to run constructor for record")
                .assertCauseMessageContains("y must be between");
    }

    @Test
    void succeed_whenClassHasDualPrecondition_givenPrefabValueForBothFields() {
        EqualsVerifier
                .forClass(DualPrecondition.class)
                .withPrefabValuesForField("x", iRed, iBlue)
                .withPrefabValuesForField("y", 505, 555)
                .verify();
    }

    @Test
    void succeed_whenClassHasDualPrecondition_givenPrefabValueForBothFields_redCopy() {
        EqualsVerifier
                .forClass(DualPrecondition.class)
                .withPrefabValuesForField("x", iRed, iBlue, iRed)
                .withPrefabValuesForField("y", 505, 555, 505)
                .verify();
    }

    @Test
    void succeed_whenClassHasDualPrecondition_givenPrefabValueForBothFields_record() {
        EqualsVerifier
                .forClass(DualPreconditionRecord.class)
                .withPrefabValuesForField("x", iRed, iBlue)
                .withPrefabValuesForField("y", 505, 555)
                .verify();
    }

    @Test
    void succeed_whenClassHasDualPrecondition_givenPrefabValueForBothFields_record_redCopy() {
        EqualsVerifier
                .forClass(DualPreconditionRecord.class)
                .withPrefabValuesForField("x", iRed, iBlue, iRed)
                .withPrefabValuesForField("y", 505, 555, 505)
                .verify();
    }

    @Test
    void succeed_whenClassHasStringPrecondition_givenPrefabValueForField() {
        EqualsVerifier
                .forClass(StringPrecondition.class)
                .withPrefabValuesForField("s", "precondition:red", "precondition:blue")
                .verify();
    }

    @Test
    void succeed_whenClassHasStringPrecondition_givenPrefabValueForField_redCopy() {
        EqualsVerifier
                .forClass(StringPrecondition.class)
                .withPrefabValuesForField("s", "precondition:red", "precondition:blue", "precondition:red")
                .verify();
    }

    @Test
    void throw_whenFieldDoesNotExistInClass() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("doesnt_exist", 1, 2))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition:", "does not contain field doesnt_exist");
    }

    @Test
    void throw_whenFieldDoesNotExistInClass_redCopy() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("doesnt_exist", 1, 2, 1))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition:", "does not contain field doesnt_exist");
    }

    @Test
    void throw_whenFirstPrefabValueIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", null, pBlue))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenSecondPrefabValueIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, null))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenThirdPrefabValueIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, pBlue, null))
                .assertThrows(NullPointerException.class);
    }

    @Test
    void throw_whenThePrefabValuesAreTheSame() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, pRed))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values for field `FinalPoint point` are equal");
    }

    @Test
    void throw_whenThePrefabValuesAreEqual() {
        FinalPoint red1 = new FinalPoint(3, 4);
        FinalPoint red2 = new FinalPoint(3, 4);

        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", red1, red2))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values for field `FinalPoint point` are equal");
    }

    @Test
    void throw_whenRedCopyIsDifferentFromRed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, pBlue, pBlue))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "red and redCopy prefab values of type FinalPoint should be equal but are not");
    }

    @Test
    void throw_whenRedCopyIsSameInstanceAsRed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, pBlue, pRed))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "red and redCopy prefab values of type FinalPoint are the same object");
    }

    @Test
    void throw_whenFieldsDontMatch() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SinglePrecondition.class).withPrefabValuesForField("point", 1, 2))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "for field point should be of type FinalPoint but are");
    }

    @Test
    void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade() {
        EqualsVerifier
                .forClass(OtherModuleContainer.class)
                .withPrefabValuesForField("date", LocalDate.of(2024, 9, 18), LocalDate.of(2024, 9, 19))
                .verify();
    }

    @Test
    void dontThrow_whenAddingPrefabValuesFromAnotherModuleWithAnExplicitRedCopy() {
        EqualsVerifier
                .forClass(OtherModuleContainer.class)
                .withPrefabValuesForField(
                    "date",
                    LocalDate.of(2024, 9, 18),
                    LocalDate.of(2024, 9, 19),
                    LocalDate.of(2024, 9, 18))
                .verify();
    }

    @Test
    void succeed_whenPrefabForArrayIsOverridden() {
        EqualsVerifier
                .forClass(ThrowingArrayContainer.class)
                .withPrefabValuesForField("field", new int[] { 1, 2, 3 }, new int[] { 4, 5, 6 })
                .verify();
    }

    @Test
    void succeed_whenClassContainsSomethingThatAllowsSubclassesAndASubclassIsGiven() {
        EqualsVerifier
                .forClass(ListContainer.class)
                .withPrefabValuesForField("list", List.of("x"), List.of("y"))
                .verify();
    }

    @Test
    void succeed_whenClassContainsAGenericInterfaceThatRefersToItself() {
        DifficultGeneric one = new DifficultGeneric(List.of());
        DifficultGeneric two = new DifficultGeneric(null);
        EqualsVerifier
                .forClass(DifficultGeneric.class)
                .withPrefabValuesForField("list", List.of(one), List.of(two))
                .verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenFieldExistsInSuperclass() {
        EqualsVerifier.forClass(SubPrecondition.class).withPrefabValuesForField("point", pRed, pBlue).verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenFieldExistsInSuperclass_redCopy() {
        EqualsVerifier
                .forClass(SubPrecondition.class)
                .withPrefabValuesForField("point", pRed, pBlue, pRedCopy)
                .verify();
    }

    static final class OtherModuleContainer {

        private final LocalDate date;

        public OtherModuleContainer(LocalDate date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object obj) {
            if (date == null || date.getMonth() != Month.SEPTEMBER) {
                throw new IllegalArgumentException("date must be in September! But was " + date);
            }
            if (!(obj instanceof OtherModuleContainer)) {
                return false;
            }
            OtherModuleContainer other = (OtherModuleContainer) obj;
            return Objects.equals(date, other.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date);
        }
    }

    static final class ThrowingArrayContainer {

        private final int[] field;

        public ThrowingArrayContainer(int[] field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThrowingArrayContainer)) {
                return false;
            }
            if (field != null && field.length == 1) {
                throw new IllegalStateException("Don't use a built-in prefab value!");
            }
            ThrowingArrayContainer other = (ThrowingArrayContainer) obj;
            return Arrays.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(field);
        }
    }

    static final class ListContainer {

        private final List<String> list;

        public ListContainer(List<String> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListContainer)) {
                return false;
            }
            ListContainer other = (ListContainer) obj;
            return Objects.equals(list, other.list);
        }

        @Override
        public int hashCode() {
            return Objects.hash(list);
        }
    }

    static final class DifficultGeneric {

        private final List<DifficultGeneric> list;

        public DifficultGeneric(List<DifficultGeneric> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DifficultGeneric)) {
                return false;
            }
            DifficultGeneric other = (DifficultGeneric) obj;
            return Objects.equals(list, other.list);
        }

        @Override
        public int hashCode() {
            return Objects.hash(list);
        }
    }

    static class SuperPrecondition {

        protected final FinalPoint point;

        public SuperPrecondition(FinalPoint point) {
            this.point = point;
        }
    }

    static final class SubPrecondition extends SuperPrecondition {

        public SubPrecondition(FinalPoint point) {
            super(point);
        }

        @Override
        public boolean equals(Object obj) {
            if (point == null || point.getX() != 3) {
                throw new IllegalArgumentException("x coordinate must be 3! But was " + point);
            }
            if (!(obj instanceof SubPrecondition)) {
                return false;
            }
            SubPrecondition other = (SubPrecondition) obj;
            return Objects.equals(point, other.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }
}
