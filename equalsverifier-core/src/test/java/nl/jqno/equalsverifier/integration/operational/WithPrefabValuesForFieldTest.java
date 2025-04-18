package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

class WithPrefabValuesForFieldTest {

    private final FinalPoint pRed = new FinalPoint(3, 42);
    private final FinalPoint pBlue = new FinalPoint(3, 1337);
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
    void succeed_whenClassHasSinglePrecondition_givenPrefabValuesForField() {
        EqualsVerifier.forClass(SinglePrecondition.class).withPrefabValuesForField("point", pRed, pBlue).verify();
    }

    @Test
    void fail_whenClassHasDualPrecondition() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(DualPrecondition.class).verify())
                .assertFailure()
                .assertMessageContains("x must be between");
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
    void succeed_whenClassHasDualPrecondition_givenPrefabValueForBothFields() {
        EqualsVerifier
                .forClass(DualPrecondition.class)
                .withPrefabValuesForField("x", iRed, iBlue)
                .withPrefabValuesForField("y", 505, 555)
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
    void throw_whenThePrefabValuesAreTheSame() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(SinglePrecondition.class)
                            .withPrefabValuesForField("point", pRed, pRed))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
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
                .assertMessageContains("Precondition", "both prefab values of type FinalPoint are equal");
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
                .withPrefabValuesForField("list", Collections.singletonList("x"), Collections.singletonList("y"))
                .verify();
    }

    @Test
    void succeed_whenClassContainsAGenericInterfaceThatRefersToItself() {
        DifficultGeneric one = new DifficultGeneric(new ArrayList<>());
        DifficultGeneric two = new DifficultGeneric(null);
        EqualsVerifier
                .forClass(DifficultGeneric.class)
                .withPrefabValuesForField("list", Collections.singletonList(one), Collections.singletonList(two))
                .verify();
    }

    @Test
    void succeed_whenClassHasSinglePrecondition_givenFieldExistsInSuperclass() {
        EqualsVerifier.forClass(SubPrecondition.class).withPrefabValuesForField("point", pRed, pBlue).verify();
    }

    static final class SinglePrecondition {

        private final FinalPoint point;

        public SinglePrecondition(FinalPoint point) {
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (point == null || point.getX() != 3) {
                throw new IllegalArgumentException("x coordinate must be 3! But was " + point);
            }
            if (!(obj instanceof SinglePrecondition)) {
                return false;
            }
            SinglePrecondition other = (SinglePrecondition) obj;
            return Objects.equals(point, other.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }

    static final class DualPrecondition {

        private final int x;
        private final int y;

        public DualPrecondition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (x < 100 || x > 200) {
                throw new IllegalArgumentException("x must be between 100 and 200! But was " + x);
            }
            if (y < 500 || y > 600) {
                throw new IllegalArgumentException("y must be between 500 and 600! But was " + y);
            }
            if (!(obj instanceof DualPrecondition)) {
                return false;
            }
            DualPrecondition other = (DualPrecondition) obj;
            return Objects.equals(x, other.x) && Objects.equals(y, other.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static final class StringPrecondition {

        private final String s;

        public StringPrecondition(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (s != null && !s.startsWith("precondition:")) {
                throw new IllegalArgumentException("bad precondition: " + s);
            }
            if (!(obj instanceof StringPrecondition)) {
                return false;
            }
            StringPrecondition other = (StringPrecondition) obj;
            return Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
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

    public final class DifficultGeneric {

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
